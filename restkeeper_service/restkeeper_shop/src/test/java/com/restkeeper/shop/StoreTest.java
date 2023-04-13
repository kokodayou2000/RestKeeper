package com.restkeeper.shop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StoreTest extends BaseTest {
    @Reference(version = "1.0.0",check = false)
    private IStoreService storeService;

    @Test
    @Rollback(value = false)
    public void saveTest(){
        //没有设置shopId的值,但是经过拦截器，会默认设置为 'test'
        Store store = new Store();
        store.setBrandId("test1");
        store.setStoreName("测试");
        store.setProvince("北京");
        store.setCity("昌平区");
        store.setArea("金燕龙大厦");
        store.setAddress("北京 昌平区 金燕龙大厦");
        storeService.save(store);
    }

    @Test
    public void queryTest(){
        System.out.println(storeService.getById("1646173968631119873"));
    }


}
