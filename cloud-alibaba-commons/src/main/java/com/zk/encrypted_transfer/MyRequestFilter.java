package com.zk.encrypted_transfer;

import com.zk.commons.exception.ServiceException;
import com.zk.sgcc.Sm2Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 密文传输
 * OncePerRequestFilter
 * 处理GET请求参数加密传输
 * 对GET请求的加密参数data进行解密后封装成 Map<String, String[]> 传入自定已的DecryptRequestWrapper
 * 服务中配置方法 {@see CipherConfig})
 */
public class MyRequestFilter extends OncePerRequestFilter {

    private String privateKeyA;

    private List<String> ignoreUris;

    public MyRequestFilter(String privateKeyA, List<String> ignoreUris) {
        this.privateKeyA = privateKeyA;
        this.ignoreUris = ignoreUris;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        AntPathMatcher matcher = new AntPathMatcher();
        // 过滤传入明文的uri
        if (!CollectionUtils.isEmpty(ignoreUris)) {
            for (String url : ignoreUris) {
                if (matcher.match(url, request.getRequestURI())) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }


        Map<String, String[]> parameterMap = new HashMap<>();
        String enCodeStr = request.getParameter("data");
        if (StringUtils.isEmpty(enCodeStr)) {
            filterChain.doFilter(request, response);
        } else {
            String deCodeStr = "";
            // 解密前端的密文加上04
            enCodeStr = "04" + enCodeStr;
            //  解密
            try {
                deCodeStr = Sm2Utils.decrypt(enCodeStr, privateKeyA);
            } catch (Exception e) {
                throw new ServiceException("解密失败");
            }

            // 解密成功后封装parameterMap
            for (String s : deCodeStr.split("&")) {
                String[] kv = s.split("=");
                String k = kv[0];
                String v = null;
                if (kv.length == 2) {
                    v = kv[1];
                }

                if (parameterMap.containsKey(k)) {
                    String[] values = parameterMap.get(k);
                    if (Objects.isNull(values)) {
                        values = new String[]{};
                    }
                    // 添加一个元素到数组中
                    List<String> list = new ArrayList(Arrays.asList(values));//**须定义时就进行转化**
                    if (v != null) {
                        list.add(v);
                    }
                    String[] afterValues = new String[list.size()];
                    list.toArray(afterValues);
                    parameterMap.put(k, afterValues);
                } else {
                    if (v != null) {
                        String[] values = new String[]{v};
                        parameterMap.put(k, values);
                    } else {
                        parameterMap.put(k, null);
                    }
                }
            }


            filterChain.doFilter(new DecryptRequestWrapper(request, parameterMap), response);
        }

    }

}
