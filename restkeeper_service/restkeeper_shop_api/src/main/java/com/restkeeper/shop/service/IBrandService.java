package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.Brand;

public interface IBrandService extends IService<Brand> {

    //分页查询（降序）
    IPage<Brand> queryPage(int pageNo,int pageSize);



}
