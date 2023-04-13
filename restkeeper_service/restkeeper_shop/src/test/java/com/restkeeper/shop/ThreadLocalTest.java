package com.restkeeper.shop;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalTest {
    static ThreadLocal<Map<String,Object>> tl = ThreadLocal.withInitial(() -> new HashMap<>());
//    static ThreadLocal<String> tl = ThreadLocal.withInitial(String::new);

    public static void main(String[] args) {
        tl.get().put("main","main");


        outInfo();

        new Thread(()->{
            tl.get().put("k1","v1");
            outInfo();
        },"t1").start();

        new Thread(()->{
            //可以存放多个
//            tl.get().put("k2","v2");
            Map<String,Object> m1 = new HashMap<>();
            m1.put("m1","m1");
            tl.set(m1);
            tl.get().put("111","111");
//            tl.get().put("k2k","v2v");
            Map<String,Object> m2 = new HashMap<>();
            m2.put("m2","m2");
            tl.set(m2);
            tl.get().put("222","222");
            outInfo();
        },"t2").start();

    }

    private static void outInfo() {
        System.out.println(Thread.currentThread().getName()+"  "+
                tl.get()
        );
    }


}
