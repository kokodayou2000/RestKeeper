package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.mapper.OperatorUserMapper;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//@Service("operatorUserService")
@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope //动态刷新
public class OperatorUserServiceImpl extends ServiceImpl<OperatorUserMapper, OperatorUser> implements IOperatorUserService{


    @Value("${gateway.secret}")
    private String secret;



    //根据name进行分页数据查询
    @Override
    public IPage<OperatorUser> queryPageByName(int pageNum, int pageSize, String name) {


        IPage<OperatorUser> page = new Page<>(pageNum,pageSize);

        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)){
            queryWrapper.like("loginname",name);
        }
        return this.page(page,queryWrapper);
    }

    //执行登录的时候，业务层的具体实现
    @Override
    public Result login(String loginName, String loginPass) {
        Result result = new Result();
        //参数校验
        if (StringUtils.isEmpty(loginName)){
            result.setStatus(ResultCode.error);
            result.setDesc("用户名不存在");
            return result;
        }

        if (StringUtils.isEmpty(loginPass)){
            result.setStatus(ResultCode.error);
            result.setDesc("密码不存在");
            return result;
        }

        //查询用户是否存在
        QueryWrapper<OperatorUser> wrapper = new QueryWrapper<>();
        wrapper.eq("loginname",loginName);
        OperatorUser operatorUser = this.getOne(wrapper);
        if (null == operatorUser){
            //未查询到用户信息
            result.setStatus(ResultCode.error);
            result.setDesc("用户不存在");
            return result;
        }

        //密码校验
        //md5只能进行加密，不能解密的

        //md5加密的密文
        //通过工具类获取该密文的盐值
        //通过官方提供的md5算法对原文和盐值进行加密运算，得到原文与盐值的密文
        //得到的密文和数据库的md5已经加密过的进行对比




        String salts = MD5CryptUtil.getSalts(operatorUser.getLoginpass());
        //通过盐值和实际的字符串进行Md5加密，如果和数据库的密文相同就表示能进行登录
        if (!Md5Crypt.md5Crypt(loginPass.getBytes(StandardCharsets.UTF_8),salts).equals(operatorUser.getLoginpass())){
            //密码不正确
            result.setStatus(ResultCode.error);
            result.setDesc("密码不正确");
            return result;
        }

        //生成jwt令牌
        Map<String,Object> tokenInfo = Maps.newHashMap();
        tokenInfo.put("loginName",operatorUser.getLoginname());

        String token = null;
        try {
            //根据secret 和用户名称生成令牌
            token = JWTUtil.createJWTByObj(tokenInfo,secret);
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            return result;
        }


        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(operatorUser);
        result.setToken(token);

        return result;
    }



}
