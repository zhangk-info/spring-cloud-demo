package com.zk.importbigdata.imports;

public class Page {

    private int pageNum;
    private int pageSize;
    private int startRow;
    private int endRow;
    private int total;
    private int pages;

    public Page() {
    }

    public Page(int pageNum, int pageSize, int total) {
        if (pageNum == 1 && pageSize == 2147483647) {
            pageSize = 0;
        }

        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.setTotal(total);
        this.calculateStartAndEndRow();
    }

    public Page(int pageNum, int pageSize) {
        if (pageNum == 1 && pageSize == 2147483647) {
            pageSize = 0;
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.calculateStartAndEndRow();
    }

    public int getPages() {
        return this.pages;
    }

    public int getEndRow() {
        return this.endRow;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public Page setPageNum(int pageNum) {
        this.pageNum = pageNum <= 0 ? 1 : pageNum;
        this.calculateStartAndEndRow();
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getStartRow() {
        return this.startRow;
    }

    public long getTotal() {
        return (long) this.total;
    }

    public void setTotal(int total) {
        this.total = total;
        if (total == -1) {
            this.pages = 1;
        } else {
            if (this.pageSize > 0) {
                this.pages = total / this.pageSize + (total % this.pageSize == 0 ? 0 : 1);
            } else {
                this.pages = 0;
            }

            if (this.pageNum > this.pages) {
                this.pageNum = this.pages;
                this.calculateStartAndEndRow();
            }

        }
    }

    private void calculateStartAndEndRow() {
        this.startRow = this.pageNum > 0 ? (this.pageNum - 1) * this.pageSize : 0;
        this.endRow = this.startRow + this.pageSize * (this.pageNum > 0 ? 1 : 0);
        this.endRow = this.endRow >= this.total ? this.total : this.endRow;
    }
}
