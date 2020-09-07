package com.zk.configuration.auth.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 实现UserDetails并写入自定义属性
 */
@Data
public class SecurityUserDetails implements UserDetails {
    private Long userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean enabled;
    private boolean credentialsNonExpired;

    //自定义属性
    private String nickName;
    private String accountType;

}
