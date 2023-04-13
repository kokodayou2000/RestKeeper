package com.restkeeper.shop.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.google.common.collect.Lists;
import io.swagger.models.auth.In;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyBatisPlusTenantConfig {
    //当前的多租户标识字段,这个是数据库的实际列名
    private static final String SYSTEM_TENANT_ID = "shop_id";
    //定义有那些表要忽略多租户的操作
    private static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList("");

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        System.out.println("paginationInterceptor -- ");
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //sql解析处理拦截：增加住户处理回调。
        //多租户sql解析器，sql解析处理拦截，增加租户处理回调
        TenantSqlParser tenantSqlParser = new TenantSqlParser().setTenantHandler(new TenantHandler() {

            //设置租户id
            @Override
            public Expression getTenantId(boolean where) {
                //使用上下文对象中获取到shopId
                String shopId = RpcContext.getContext().getAttachment("shopId");
                if (shopId == null){
                    throw new RuntimeException("get tenantId error");
                }
                //住户id就是test
                return new StringValue(shopId);
            }
            //设置租户id对应的表字段
            @Override
            public String getTenantIdColumn() {
                return SYSTEM_TENANT_ID;
            }
            //设置表级过滤器
            @Override
            public boolean doTableFilter(String tableName) {
                //如果有任何能匹配到的就返回true,匹配的时候忽略大小写了
                return IGNORE_TENANT_TABLES.stream().anyMatch((e)->
                    e.equalsIgnoreCase(tableName)
                );
            }
        });
        paginationInterceptor.setSqlParserList(Lists.newArrayList(tenantSqlParser));
        return paginationInterceptor;
    }
}
