package com.restkeeper.controller.shop;

import com.restkeeper.response.vo.PageVO;
import com.restkeeper.shop.entity.Brand;
import com.restkeeper.shop.service.IBrandService;
import com.restkeeper.vo.shop.AddTBrandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"品牌管理"})
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference(version = "1.0.0",check = false)
    private IBrandService brandService;

    @ApiOperation(value = "分页查询所有品牌信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",name = "page",value = "当前页码",required = false,dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "页面大小",required = false,dataType = "Integer")
    })
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<Brand> pageList(@PathVariable("page") Integer page,
                                  @PathVariable("pageSize") Integer pageSize){

        return new PageVO<Brand>(brandService.queryPage(page,pageSize));
    }

    @ApiOperation("新增品牌")
    @PostMapping("/add")
    public boolean add(@RequestBody AddTBrandVO brandVO){
        Brand brand = new Brand();
        BeanUtils.copyProperties(brandVO,brand);
        return brandService.save(brand);
    }
}
