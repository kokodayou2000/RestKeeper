package com.restkeeper.response.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 前端对象转换封装
 * @param <T>
 */
@Data
public class PageVO<T> {

    private  long counts; //总个数

    private  long pagesize; //每页个数

    private  long pages; //总页数

    private  long page; //当前页

    private List<T> items; //数据记录

    //IPage 是mp中提供的一个接口，实现了一些固定接口
    public PageVO(IPage page) {
        this.pagesize = page.getSize();
        this.counts = page.getTotal();
        this.page = page.getCurrent();
        this.pages = page.getPages();
        this.items = page.getRecords();
    }
}
