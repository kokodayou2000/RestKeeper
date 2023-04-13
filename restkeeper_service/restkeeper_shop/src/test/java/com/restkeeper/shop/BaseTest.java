package com.restkeeper.shop;

import org.apache.dubbo.rpc.RpcContext;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {
    //执行初始化操作

    @Before
    public void init(){
        RpcContext.getContext().setAttachment("shopId","t1");
    }


}
