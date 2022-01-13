package com.zk.importbigdata.format;

import com.zk.importbigdata.config.Header;

public interface ColFormat<T> {

    Object format(Header header, Object value, T row);
}
