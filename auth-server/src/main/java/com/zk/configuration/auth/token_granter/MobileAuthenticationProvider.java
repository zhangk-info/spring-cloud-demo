package com.zk.configuration.auth.token_granter;

import com.zk.configuration.auth.SecurityUserDetailService;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Setter
public class MobileAuthenticationProvider implements AuthenticationProvider {
    private SecurityUserDetailService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        MobileAuthenticationToken authenticationToken = (MobileAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        // todo 获取短信验证码
//        UserDetails user = userDetailsService.loadUserByMobile(mobile);
//        if (user == null) {
//            throw new InternalAuthenticationServiceException("手机号或密码错误");
//        }
//        if (!passwordEncoder.matches(code, user.getPassword())) {
//            throw new BadCredentialsException("手机号或密码错误");
//        }
        MobileAuthenticationToken authenticationResult = null;//new MobileAuthenticationToken(user, code, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}