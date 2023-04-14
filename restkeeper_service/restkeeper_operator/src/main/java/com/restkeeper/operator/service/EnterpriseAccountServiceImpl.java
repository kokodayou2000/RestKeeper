package com.restkeeper.operator.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;

import com.restkeeper.operator.config.RabbitMQConfig;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import com.restkeeper.sms.SmsObject;
import com.restkeeper.utils.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount>  implements IEnterpriseAccountService{

    //注入模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${sms.operator.signName}")
    private String signName;

    @Value("${sms.operator.templateCode}")
    private String templateCode;

    @Value("${gateway.secret}")
    private String secret;


    //消息的发送
    private void sendMessage(String phone,String shopId,String pwd){
        //将smsObject转换成json，然后发送
        SmsObject smsObject = new SmsObject();

        //数据封装
        smsObject.setPhoneNumber(phone);
        smsObject.setSignName(signName);
        smsObject.setTemplateCode(templateCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shopId",shopId);
        jsonObject.put("password",pwd);

        //完成json字符串的转换
        smsObject.setTemplateJsonParam(jsonObject.toJSONString());
        rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE, JSON.toJSONString(smsObject));

    }



    /**
     * 按照企业名称并模糊查询并分页
     * @param pageNum
     * @param pageSize
     * @param enterpriseName
     * @return
     */
    @Override
    public IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String enterpriseName) {
        IPage<EnterpriseAccount> page = new Page<>(pageNum,pageSize);
        QueryWrapper<EnterpriseAccount> querywrapper = new QueryWrapper<>();
        //如果传入企业名称才会进行查询
        if (StringUtils.isNotEmpty(enterpriseName)){
            querywrapper.like("enterprise_name",enterpriseName);
        }

        return this.page(page,querywrapper);
    }

    @Override
    @Transactional
    public boolean add(EnterpriseAccount account) {
        boolean flag  = true;
        try{
            //账号，密码特殊处理
            String shopId = getShopId();
            account.setShopId(shopId);

            //生成密码 6位
            String pwd = RandomStringUtils.randomNumeric(6);

            account.setPassword(Md5Crypt.md5Crypt(pwd.getBytes(StandardCharsets.UTF_8)));
            //保存
            this.save(account);

            //发送消息.将创建的账号发送给用户
            sendMessage(account.getPhone(),shopId,pwd);

        }catch (Exception e){
            flag = false;
            throw e;
        }

        return flag;
    }

    //账号还原
    @Override
    @Transactional
    public boolean recovery(String id) {
        //调用dao层的方法

        return this.getBaseMapper().recovery(id);

    }

    @Override
    @ApiOperation("重置密码")
    @Transactional
    public boolean restPwd(String id, String password) {
        boolean flag  = true;

        try {
            EnterpriseAccount enterpriseAccount = this.getById(id);
            if (enterpriseAccount == null){
                //不存在账号
                return false;
            }

            String newPwd ;
            //自定义密码
            if (StringUtils.isNotEmpty(password)){
                newPwd = password;

            }else {
                newPwd = RandomStringUtils.randomNumeric(6);
            }

            //将新的密码加密
            enterpriseAccount.setPassword(Md5Crypt.md5Crypt(newPwd.getBytes(StandardCharsets.UTF_8)));
            this.updateById(enterpriseAccount);
            //明文发送给用户
            sendMessage(enterpriseAccount.getPhone(),enterpriseAccount.getShopId(),newPwd);

        }catch (Exception e){
            e.printStackTrace();
            flag =  false;
            throw e;
        }

        return flag;
    }

    @Override
    public Result login(String shopId, String telPhone, String loginPwd) {
        Result result = new Result();
        if (StringUtils.isEmpty(shopId)){
            result.setStatus(ResultCode.error);
            result.setDesc("商铺id为空");
            return result;
        }
        if (StringUtils.isEmpty(telPhone)){
            result.setStatus(ResultCode.error);
            result.setDesc("手机号为空");
            return result;
        }
        if (StringUtils.isEmpty(loginPwd)){
            result.setStatus(ResultCode.error);
            result.setDesc("密码为空");
            return result;
        }



        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();


        //通过lambda创建出来对应的映射了 在实体类中的 @TableName(value="t_enterprise_account")
        //所有允许通过这种方式避免硬编码
        //不直接进行密码判断，需要获取到盐才行
        queryWrapper.lambda().eq(EnterpriseAccount::getShopId,shopId)
                .eq(EnterpriseAccount::getPhone,telPhone);


        //未禁用的状态
        queryWrapper.lambda().notIn(EnterpriseAccount::getStatus,AccountStatus.Forbidden.getStatus());

        EnterpriseAccount account = this.getOne(queryWrapper);

        if (null == account){
            result.setStatus(ResultCode.error);
            result.setDesc("账号或者密码错误");
        }

        //根据密文获取到salt
        assert account != null;
        String salts = MD5CryptUtil.getSalts(account.getPassword());

        //将明文密码和盐值生成对应的密文，判断是否和数据库中的密文是否相等
        if (!Md5Crypt.md5Crypt(loginPwd.getBytes(StandardCharsets.UTF_8),salts).equals(account.getPassword())){
            result.setStatus(ResultCode.error);
            result.setDesc("密码错误");
            return result;
        }

        Map<String,Object> tokenInfo = Maps.newHashMap();
        //将shopId放到了Jwt令牌中，为了dubbo的隐式传参
        tokenInfo.put("shopId",shopId);
        //存放登录名
        tokenInfo.put("loginName",account.getEnterpriseName());
        //存放登录类型
        //表示集团用户
        tokenInfo.put("loginType", SystemCode.USER_TYPE_SHOP);
        //转换jwt令牌
        String token = null;
        try {
            token = JWTUtil.createJWTByObj(tokenInfo,secret);
        } catch (IOException e) {
            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            log.error("加密失败"+e.getMessage());
        }
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setToken(token);
        //将对象返回
        result.setData(account);

        return result;
    }

    //获取shopId 8位的随机数字
    private String getShopId() {

        String shopId = RandomStringUtils.randomNumeric(8);

        //店铺校验
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id",shopId);
        EnterpriseAccount enterpriseAccount = this.getOne(queryWrapper);
        if (null != enterpriseAccount){
            //查询到了，需要重新生成
            return this.getShopId();
        }
        return shopId;
    }
}