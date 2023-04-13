package com.restkeeper.operator.controller;

import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.response.vo.AddEnterpriseAccountVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.response.vo.ResetPwdVO;
import com.restkeeper.response.vo.UpdateEnterpriseAccountVO;
import com.restkeeper.utils.AccountStatus;
import com.restkeeper.utils.BeanListUtils;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = {"企业账号管理"})
@RequestMapping("/enterprise")
@RestController
public class EnterpriseAccountController {

    //注入
    @Reference(version = "1.0.0",check = false)
    private IEnterpriseAccountService enterpriseAccountService;

    @ApiOperation("查询企业账号列表")
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<EnterpriseAccount> findListByPage(@PathVariable("page") int page,
                                                    @PathVariable("pageSize") int pageSize,
                                                    @RequestParam(value = "enterpriseName",required = false) String enterpriseName
                                                    ){

        return new PageVO<EnterpriseAccount>(enterpriseAccountService.queryPageByName(page,pageSize,enterpriseName));

    }

    @ApiOperation("新增账号")
    @PostMapping("/add")
    public boolean add(@RequestBody AddEnterpriseAccountVO addEnterpriseAccountVO){
        //将从前端复制过来的vo,转换成数据库的实体
        //bean拷贝
        EnterpriseAccount enterpriseAccount = new EnterpriseAccount();
        //mp提供的方法
        BeanUtils.copyProperties(addEnterpriseAccountVO, enterpriseAccount);
        //设置时间
        LocalDateTime localDateTime = LocalDateTime.now();
        enterpriseAccount.setApplicationTime(localDateTime);

        //设置过期时间
        LocalDateTime expireTime = null;
        //试用期默认7天
        if (addEnterpriseAccountVO.getStatus() == 0){
            //试用账号,默认7天过期
            expireTime = localDateTime.plusDays(7);

        }
        if (addEnterpriseAccountVO.getStatus() == 1){
            //到期时间,根据管理员设置的时间
            expireTime = localDateTime.plusDays(addEnterpriseAccountVO.getValidityDay());
        }

        if (expireTime != null){
            enterpriseAccount.setExpireTime(expireTime);
        }else {
            //异常操作
            throw new RuntimeException("账号类型信息设置有误");
        }

        return enterpriseAccountService.add(enterpriseAccount);
    }

    @ApiOperation("账号查询")
    @GetMapping("/getById/{id}")
    public EnterpriseAccount getById(@PathVariable("id") String id){
        return enterpriseAccountService.getById(id);
    }

    //账号编辑
    @ApiOperation("账号编辑")
    @PutMapping("/update")
    public Result update(@RequestBody UpdateEnterpriseAccountVO updateEnterpriseAccountVO){
        Result result = new Result();

        //查询原有企业账户信息
        EnterpriseAccount account = enterpriseAccountService.getById(updateEnterpriseAccountVO.getEnterpriseId());
        if (null == account){
            result.setStatus(ResultCode.error);
            result.setDesc("修改账号不存在");
            return result;
        }

        //修改状态信息
        if (updateEnterpriseAccountVO.getStatus() != null){
            //正式期不能修改到使用期
            //试用账号是0，正式账号是1。如果更新的vo是0，并且原有账号是1，那就不支持
            if (updateEnterpriseAccountVO.getStatus() == 0
                    && account.getStatus() == 1) {
                result.setStatus(ResultCode.error);
                return result;
            }
            //试用期账号修改成正式账号，实际更新属性值，会通过bean拷贝，下面只会进行时间的更新
            if (updateEnterpriseAccountVO.getStatus() == 1 && account.getStatus() == 0){
                //设置到期时间
                LocalDateTime now = LocalDateTime.now();
                //添加更新的时间
                LocalDateTime expireTime = now.plusDays(updateEnterpriseAccountVO.getValidityDay());
                //设置应用时间，就是你什么时候设定的
                account.setApplicationTime(now);
                //过期时间
                account.setExpireTime(expireTime);
            }
            //正式添加延期
            if (updateEnterpriseAccountVO.getStatus() == 1 && account.getStatus() == 1){
                LocalDateTime now = LocalDateTime.now();
                //设置到期时间
                LocalDateTime expireTime = now.plusDays(updateEnterpriseAccountVO.getValidityDay());
                //设置过期时间
                account.setExpireTime(expireTime);
            }
        }
        //执行bean拷贝，会将其他字段拷贝到account中
        //比如编辑面板下的企业名称之类的，更新的vo并不会有账号状态或者账号过期时间等信息的
        //执行这种bean拷贝也只是根据属性匹配
        BeanUtils.copyProperties(updateEnterpriseAccountVO,account);

        //执行修改
        boolean flag = enterpriseAccountService.updateById(account);
        if (flag){
            result.setStatus(ResultCode.success);
            result.setDesc("修改成功");
            return result;
        }else{
            //修改失败
            result.setStatus(ResultCode.error);
            result.setDesc("修改失败");
        }
        return result;
    }


    @ApiOperation("账号删除，伪删除")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable("id") String id){
        Result result = new Result();
        boolean b = enterpriseAccountService.removeById(id);
        if (b){
            result.setStatus(ResultCode.success);
            result.setDesc("账号删除成功");
        }else{
            result.setStatus(ResultCode.error);
            result.setDesc("账号删除失败");
        }
        return result;
    }

    //账号还原
    @ApiOperation("账号还原")
    @PutMapping("/recovery/{id}")
    public boolean recovery(@PathVariable("id") String id){
        return enterpriseAccountService.recovery(id);
    }


    //账号禁用
    @ApiOperation("账号禁用")
    @PutMapping("/forbidden/{id}")
    public boolean forbidden(@PathVariable("id") String id){
        //获取到id对应的实体
        EnterpriseAccount enterpriseAccount = enterpriseAccountService.getById(id);
        //设置该实体中的状态
        enterpriseAccount.setStatus(AccountStatus.Forbidden.getStatus());
        //更新
        return enterpriseAccountService.updateById(enterpriseAccount);
    }

    //重置密码
    @ApiOperation("密码重置")
    @PutMapping("/restPwd")
    public boolean restPwd(@RequestBody ResetPwdVO pwdVo){
        return enterpriseAccountService.restPwd(pwdVo.getId(), pwdVo.getPwd());
    }
}
