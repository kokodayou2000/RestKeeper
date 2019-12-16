package com.restkeeper.operator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员的登录接口
 */
@RestController
public class UserController{


    @Value("${server.port}")
    private String port;

    @Value("${key:''}")
    private String key;

    @GetMapping(value = "/echo/{message}")
    public String echo(@PathVariable(value = "message") String message) {
        return "Hello Nacos Discovery " + message + ", i am from port " + port;
    }

    @GetMapping(value = "/config")
    public String config() {
        return "Hello Nacos Config get "+key ;
    }

}
