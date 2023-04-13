package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.utils.Result;

public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {
    //数据分页查询
    IPage<EnterpriseAccount> queryPageByName(int pageNum,int pageSize,String enterpriseName);

    //新增账号
    boolean add(EnterpriseAccount account);

    //账号还原
    boolean recovery(String id);

    //重置密码
    boolean restPwd(String id,String password);

    /**
     * 实现企业用户登录
     * @param shopId    商铺id
     * @param telPhone  电话号码
     * @param loginPwd 密码
     * @return 登录结果
     */
    Result login(String shopId,String telPhone,String loginPwd);
}
