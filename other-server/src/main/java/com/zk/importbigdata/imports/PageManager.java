package com.zk.importbigdata.imports;

import com.zk.importbigdata.imports.Page;

import java.util.List;

public class PageManager<T> extends Page {

    private List<T> datas;

    public PageManager(int pageSize, List<T> datas) {
        super(1, pageSize, datas != null && datas.size() != 0 ? datas.size() : 0);
        this.datas = datas;
    }

    public List<T> getData(int page) {
        this.setPageNum(page);
        if (this.getStartRow() > this.getTotal()) {
            return null;
        }
        return this.datas.subList(this.getStartRow(), this.getEndRow());
    }
}