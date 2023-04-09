package com.restkeeper.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.restkeeper.utils.JWTUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    //这是不需要进行网关过滤的url
    @Value("#{'${gateway.excludedUrls}'.split(',')}")
    private List<String> excludedUrls;
    //common.yml中添加的密钥
    @Value("${gateway.secret}")
    private String secret;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //获取当前请求路径
        String path = exchange.getRequest().getURI().getPath();

        //排除特殊不需要令牌的路径
        if (excludedUrls.contains(path)){
            return chain.filter(exchange);
        }

        //获取令牌信息
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isNotEmpty(token)){
            JWTUtil.VerifyResult verifyResult = JWTUtil.verifyJwt(token, secret);
            if (verifyResult.isValidate()){
                //令牌校验通过,直接放行
                return chain.filter(exchange);
            }else {
                //如果令牌错误
                HashMap<String, Object> responseData = Maps.newHashMap();
                responseData.put("code",verifyResult.getCode());
                responseData.put("message","验证失败");
                return responseError(response,responseData);
            }
        }else{
            //如果令牌不存在
            HashMap<String, Object> responseData = Maps.newHashMap();
            responseData.put("code",401);
            responseData.put("message","非法请求");
            responseData.put("cause","token is empty");
            //将responseData 转换成json放到response中
            return responseError(response,responseData);

        }
    }

    //返回响应数据
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {

        //将信息转换为json
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try{
            data = objectMapper.writeValueAsBytes(responseData);
        }catch (Exception e){
            e.printStackTrace();
        }

        //输出结果数据
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type","application/json;charset=utf-8");
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
