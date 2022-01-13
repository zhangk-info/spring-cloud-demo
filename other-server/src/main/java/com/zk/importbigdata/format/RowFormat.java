package com.zk.importbigdata.format;

public interface RowFormat<T> {
    void format(T row);
}