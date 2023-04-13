package com.restkeeper.response.interceptor;

import com.restkeeper.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 拦截器组件
 * 当用户登录的时候，执行一些token操作
 */

@Component
@Slf4j
public class WebHandlerInterceptor implements HandlerInterceptor {

    //在handler执行之前就会被调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取jwt
        String tokenInfo = request.getHeader("Authorization");
        //校验
        if (StringUtils.isNotEmpty(tokenInfo)){
            try{
                //解析令牌
                Map<String, Object> tokenMap = JWTUtil.decode(tokenInfo);
                //获取shopId
                String shopId = (String)tokenMap.get("shopId");
                //将shopId存放到RpcContext
                RpcContext.getContext().setAttachment("shopId",shopId);

            }catch (Exception e){
                log.error("解析命令失败");
                e.printStackTrace();
            }
        }
        return true;
    }

}
