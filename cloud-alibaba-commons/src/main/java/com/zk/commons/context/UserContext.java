package com.zk.commons.context;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.zk.commons.auth.SecurityUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户上下文
 * </p>
 *
 * @since 2019-08-14
 */
public final class UserContext {

    private static final ThreadLocal<CurrentUser> USER_LOCAL = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(CurrentUser user) {
        USER_LOCAL.set(user);
    }

    public static CurrentUser get() {
        return USER_LOCAL.get();
    }

    public static Long getId() {
        return Objects.isNull(get()) ? null : get().getUserId();
    }

    public static String getUsername() {
        return Objects.isNull(get()) ? null : get().getUsername();
    }

    public static String getName() {
        return Objects.isNull(get()) ? null : get().getName();
    }

    public static Integer getType() {
        return Objects.isNull(get()) ? null : get().getUserType();
    }

    /**
     * 获取认证用户信息
     *
     * @return
     */
    public static SecurityUserDetails getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            OAuth2Request clientToken = oAuth2Authentication.getOAuth2Request();
            if (!oAuth2Authentication.isClientOnly()) {
                if (authentication.getPrincipal() instanceof SecurityUserDetails) {
                    return (SecurityUserDetails) authentication.getPrincipal();
                }
                if (authentication.getPrincipal() instanceof Map) {
                    return BeanUtil.mapToBean((Map) authentication.getPrincipal(), SecurityUserDetails.class, true, CopyOptions.create());
                }
            } else {
                SecurityUserDetails openUser = new SecurityUserDetails();
                openUser.setClientId(clientToken.getClientId());
                openUser.setAuthorities(clientToken.getAuthorities());
                return openUser;
            }
        }
        return null;
    }


}
