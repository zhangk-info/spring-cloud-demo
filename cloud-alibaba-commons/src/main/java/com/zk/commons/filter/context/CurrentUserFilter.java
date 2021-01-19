package com.zk.commons.filter.context;

import com.alibaba.fastjson.JSON;
import com.zk.commons.context.CurrentUser;
import com.zk.commons.context.UserContext;
import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
import com.zk.commons.properties.URIProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CurrentUserFilter extends OncePerRequestFilter {
    @Resource
    private URIProperties urlProperties;
    @Resource
    private SignatureVerifier signerVerifier;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (matcher(request.getRequestURI())) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String authToken = request.getHeader("Authorization");
                if (null == authToken) {
                    authToken = request.getParameter("t");
                }
                if (null == authToken) {
                    throw new ServiceException(ErrorCode.NO_API_ACCESS_POWER);
                }
                Jwt jwt = JwtHelper.decodeAndVerify(StringUtils.substringAfter(authToken, "Bearer ").trim(), signerVerifier);
                CurrentUser currentUser = JSON.parseObject(jwt.getClaims(), CurrentUser.class);

                UserContext.set(currentUser);
                log.info("current currentUser is : {}", null != currentUser ? currentUser.toString() : " null");
            } catch (Exception e) {
                log.error("current user filter error. url is [{}]", request.getRequestURI(), e);
                response.setStatus(401);
                response.setCharacterEncoding("utf-8");
                response.getWriter().print(e.getMessage());
            }
            
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 匹配需要放开的路径
     *
     * @param requestUrl
     * @return
     */
    private boolean matcher(String requestUrl) {

        for (String url : urlProperties.getIgnores()) {
            if (antPathMatcher.match(url, requestUrl)) {
                return true;
            }
        }

        for (String url : urlProperties.getPublicIgnores()) {
            if (antPathMatcher.match(url, requestUrl)) {
                return true;
            }
        }

        return false;
    }
}

