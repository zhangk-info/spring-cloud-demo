package com.zk.importbigdata.config;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MergeConfig {
    private boolean allowUpdate = false;
    private Map<String, Header> loginPk;
    private List<Header> header;
    private Class distClazz;

    public MergeConfig(Class distClazz, Header... headers) {
        this.distClazz = distClazz;
        if (headers != null && headers.length != 0) {
            Map<String, Header> headerMap = new LinkedHashMap();
            this.loginPk = new LinkedHashMap();
            Header[] var4 = headers;
            int var5 = headers.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Header header = var4[var6];
                if (header.isLogicPk()) {
                    this.loginPk.put(header.getName(), header);
                }

                if (headerMap.containsKey(header.getName())) {
                    throw new RuntimeException("不允许有重复字段");
                }

                try {
                    Field field = distClazz.getDeclaredField(header.getName());
                    header.setType(field.getType());
                } catch (Exception var9) {
                    throw new RuntimeException(String.format("%s在%s中不存在", header.getName(), distClazz.getSimpleName()));
                }

                headerMap.put(header.getName(), header);
            }

            this.header = Arrays.asList(headers);
        } else {
            throw new RuntimeException("header 必须指定");
        }
    }

    public boolean isAllowUpdate() {
        return this.allowUpdate;
    }

    public void setAllowUpdate(boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public Map<String, Header> getLoginPk() {
        return this.loginPk;
    }

    public void setLoginPk(Map<String, Header> loginPk) {
        this.loginPk = loginPk;
    }

    public List<Header> getHeader() {
        return this.header;
    }

    public void setHeader(List<Header> header) {
        this.header = header;
    }

    public Class getDistClazz() {
        return this.distClazz;
    }

    public void setDistClazz(Class distClazz) {
        this.distClazz = distClazz;
    }
}
