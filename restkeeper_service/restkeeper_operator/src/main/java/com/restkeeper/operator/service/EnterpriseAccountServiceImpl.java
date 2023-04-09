package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service(version = "1.0.0",protocol = "dubbo")
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount>  implements IEnterpriseAccountService{

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

        }catch (Exception e){
            flag = false;
            throw e;
        }

        return flag;
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
