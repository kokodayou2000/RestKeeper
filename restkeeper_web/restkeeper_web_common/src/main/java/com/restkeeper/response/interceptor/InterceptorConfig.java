package com.restkeeper.response.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 过滤器配置类，这个配置类会对登录请求进行放行
 * 拦截器不需要对登录请求进行拦截
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor MyInterceptor() {
        return new WebHandlerInterceptor();
    }
    //对login的排除
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(MyInterceptor());
        // 拦截所有、排除
        interceptor.addPathPatterns("/**")
                .excludePathPatterns("/login","/login");
    }


    //swagger load
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
//        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
//        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars");
//    }

    /**
     * 跨域支持配置 允许get put delete post options的相关请求
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("*")
                .allowedMethods("GET", "PUT", "DELETE", "POST", "OPTIONS")
                .maxAge(3600);
    }


}

