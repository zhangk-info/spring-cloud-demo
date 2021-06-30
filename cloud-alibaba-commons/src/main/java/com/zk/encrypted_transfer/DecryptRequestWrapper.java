package com.zk.encrypted_transfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

/**
 * 密文传输
 * 自定义 HttpServletRequestWrapper
 * 重写HttpServletRequestWrapper的取值方法
 * 改成从自定义的parameterMap中取值
 */
public class DecryptRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> parameterMap;

    public DecryptRequestWrapper(HttpServletRequest servletRequest, Map<String, String[]> parameterMap) {
        super(servletRequest);
        this.parameterMap = parameterMap;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = parameterMap.get(parameter);
        if (values == null) {
            return null;
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public String getParameter(String parameter) {
        String[] value = parameterMap.get(parameter);
        if (value == null) {
            return null;
        }
        return value[0];
    }
}
