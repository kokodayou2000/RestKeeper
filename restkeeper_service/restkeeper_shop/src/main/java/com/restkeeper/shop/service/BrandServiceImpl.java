package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.mapper.BrandMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0",protocol = "dubbo")
@Slf4j
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {

    @Override
    public IPage<Brand> queryPage(int pageNo, int pageSize) {
        IPage<Brand> page = new Page<>(pageNo,pageSize);
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        //执行查询，用降序排序
        queryWrapper.lambda()
                .orderByDesc(Brand::getLastUpdateTime);
        return this.page(page,queryWrapper);
    }
}
