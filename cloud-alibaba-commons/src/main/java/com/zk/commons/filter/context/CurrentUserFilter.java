package com.zk.commons.filter.context;

import com.alibaba.fastjson.JSON;
import com.zk.commons.context.CurrentUser;
import com.zk.commons.context.UserContext;
import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
import com.zk.commons.properties.URIProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CurrentUserFilter extends OncePerRequestFilter {
    @Resource
    private URIProperties urlProperties;
    @Resource
    private SignatureVerifier signerVerifier;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static void renderJson(HttpServletResponse response, Object jsonObject) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(jsonObject));
        } catch (IOException e) {
            // do something
        }
    }

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
                // 设置CurrentUser
                UserContext.set(currentUser);
                log.debug("current currentUser is : {}", null != currentUser ? currentUser.toString() : " null");
                setAuthorizationWithAuthority(request);
                log.debug("set authority success : {}", SecurityContextHolder.getContext().getAuthentication().getCredentials());
            } catch (Exception e) {
                log.error("current user filter error. url is [{}]", request.getRequestURI(), e);
                response.setStatus(401);
                response.setCharacterEncoding("utf-8");
                response.getWriter().print(e.getMessage());
                return;
            }

            filterChain.doFilter(request, response);
        }
    }

    /**
     * 重新设置SecurityContext的Authentication，用于重新设置roles
     *
     * @param request
     */
    private void setAuthorizationWithAuthority(HttpServletRequest request) {
        // TODO 得到当前用户的所有权限
        List<String> roles = new ArrayList<>();
        roles.add("user");
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        for (String role : roles) {
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
            list.add(grantedAuthority);
        }
        // 生成新的Authentication
        PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(
                UserContext.getUser(), null, list
        );
        auth.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
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

