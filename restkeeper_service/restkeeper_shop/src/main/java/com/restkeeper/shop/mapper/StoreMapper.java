package com.restkeeper.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.shop.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    //查询品牌关联的门店总数
    @Select("select count(1) from t_store where brand_id=#{brandId} and status = 1 and is_deleted = 0 )")
    Integer getStoreCount(@Param("brandId")String brandId);

    //查询品牌关联的城市总数,需要进行去重操作
    @Select("select count(distinct(city)) from t_store where brand_id=#{brandId} and status = 1 and is_deleted = 0  )")
    Integer getCityCount(@Param("brandId") String brandId);

}
