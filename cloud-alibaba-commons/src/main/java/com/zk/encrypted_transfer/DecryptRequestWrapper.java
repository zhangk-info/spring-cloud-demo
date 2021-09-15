package com.zk.encrypted_transfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

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

    /**
     * 这个方法重写的目的是：
     * ?data=encryptStr...的时候只能能达到parameterNames ["data"]
     * 当接收使用对象是(UserInfo(String name;String sex;))并在resolver对对象赋值时会没有对参数名称为name和sex进行赋值（因为只有data）
     * 所以将解密的后key同时返回
     * 注意：此时对象或者参数名称中不应该有data，即任何属性名为data不可用
     *
     * @return
     */
    @Override
    public Enumeration<String> getParameterNames() {
        Set<String> names = new LinkedHashSet<>();
        names.addAll(Collections.list(super.getParameterNames()));
        names.addAll(parameterMap.keySet());
        return Collections.enumeration(names);
    }
}
