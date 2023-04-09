package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.EnterpriseAccount;

public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {
    //数据分页查询
    IPage<EnterpriseAccount> queryPageByName(int pageNum,int pageSize,String enterpriseName);

    //新增账号
    boolean add(EnterpriseAccount account);
}
