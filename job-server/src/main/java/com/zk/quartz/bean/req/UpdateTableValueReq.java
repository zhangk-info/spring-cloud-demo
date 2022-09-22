package com.zk.quartz.bean.req;

import lombok.Data;

@Data
public class UpdateTableValueReq {
    private String tableName;
    private String setColumnName;
    private String setValue;
    private String conColumnName;
    private String conValue;
}
