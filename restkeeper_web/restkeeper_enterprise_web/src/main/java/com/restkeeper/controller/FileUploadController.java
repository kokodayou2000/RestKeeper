package com.restkeeper.controller;

import com.aliyun.oss.OSSClient;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Api(tags = {"图片上传的通用接口"})
@RefreshScope   
public class FileUploadController {

    //注入oss的客户端
    @Autowired
    private OSSClient ossClient;

    @Value("${bucketName}")
    private String bucketName;
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile multipartFile){

        Result result = new Result();
        //执行图片上传
        //设置图片上传的文件名称 时间戳_文件原始名称
        String fileName = System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();
        try {
            ossClient.putObject(bucketName,fileName,multipartFile.getInputStream());
            //获取上传文件的路径，并返回给前端
            String logoPath = "https://"+bucketName+"."+endpoint+"/"+fileName;
            result.setData(logoPath);
            result.setStatus(ResultCode.success);
            result.setDesc("上传成功");

        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("上传失败");
        }


        return result;
    }


}
