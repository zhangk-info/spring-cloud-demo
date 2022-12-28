package com.zk.configuration;

import com.zk.commons.auth.SecurityUserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

/**
 * 该类用于将自定义的SecurityUserDetails 添加的UserDetails中没有属性
 * 也加入到生成的token中去，会在jwt.getClaims()中提现，以便在CurrentUser和
 * Jwt jwt = JwtHelper.decodeAndVerify(StringUtils.substringAfter(authToken, "Bearer ").trim(), signerVerifier);
 * CurrentUser currentUser = JSON.parseObject(jwt.getClaims(), CurrentUser.class);
 * 使用
 */
public class JwtInfoConvert extends DefaultAccessTokenConverter {

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        //调用父类的
        Map<String, Object> response = (Map<String, Object>) super.convertAccessToken(token, authentication);
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUserDetails) {
            SecurityUserDetails userDetails = (SecurityUserDetails) principal;
            response.put("domain", userDetails.getDomain());
            // 自定义属性
            response.put("userId", userDetails.getUserId());
            response.put("username", userDetails.getUsername());
            response.put("userType", userDetails.getUserType());

            // 其他认证属性
            response.put("iss", "http://cn.com.kcgroup");
        }
        return response;
    }
}
