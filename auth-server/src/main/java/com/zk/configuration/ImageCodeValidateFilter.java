package com.zk.configuration;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.zk.auth.captch.exception.ImageCodeException;
import com.zk.auth.captch.services.ImageCodeStore;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 验证码验证filter
 */
public class ImageCodeValidateFilter extends OncePerRequestFilter {

    @Resource
    private ImageCodeStore imageCodeStore;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equals("/oauth/token", request.getRequestURI()) &&
                StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            String code = request.getParameter("code");
            String signature = request.getParameter("signature");
            try {
                if (StrUtil.isNotEmpty(code)) {
                    imageCodeStore.validated(signature, code);
                }
            } catch (ImageCodeException e) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, e);
                return;
            }
            imageCodeStore.remove(signature);
        }
        filterChain.doFilter(request, response);
    }

}
