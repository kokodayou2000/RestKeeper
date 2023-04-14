package com.restkeeper.controller;

import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.utils.Result;
import com.restkeeper.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api("登录接口")
public class LoginController {
    @Reference(version = "1.0.0",check = false)
    private IEnterpriseAccountService enterpriseAccountService;

    @PostMapping("/login")
    @ApiOperation(value = "登录入口")
    @ApiImplicitParam(name = "Authorization",value = "jwt token",required = false,dataType = "String",paramType = "header")
    public Result login(@RequestBody LoginVO loginVO){
        return enterpriseAccountService.login(loginVO.getShopId(), loginVO.getPhone(), loginVO.getPassword());
    }

}
