package com.zk.configuration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.auth.user.entity.User;
import com.zk.auth.user.mapper.UserMapper;
import com.zk.commons.auth.SecurityUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SecurityUserDetailService implements UserDetailsService {

    public final static int ACCOUNT_STATUS_DISABLE = 0;//禁用
    public final static int ACCOUNT_STATUS_NORMAL = 1;//正常
    public final static int ACCOUNT_STATUS_LOCKED = 2;//锁定

    @Resource
    private UserMapper userMapper;


    @Override
    public SecurityUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        wrapper.and(accountQueryWrapper ->
                accountQueryWrapper/*.eq("mobile", username)
                        .or()*/.eq("username", username)
        );


        List<User> accounts = userMapper.selectList(wrapper);
        if (accounts == null || accounts.size() == 0) {
            throw new OAuth2Exception("系统用户 " + username + " 不存在!");
        }

        return buildUserDetails(accounts.get(0));
    }

    private SecurityUserDetails buildUserDetails(User user) {
        Long accountId = user.getId();
        String password = user.getPassword();
        String nickName = user.getName();
        boolean accountNonLocked = user.getStatus().intValue() != ACCOUNT_STATUS_LOCKED;
        boolean credentialsNonExpired = true;
        boolean enabled = user.getStatus().intValue() == ACCOUNT_STATUS_NORMAL ? true : false;
        boolean accountNonExpired = true;
        //设置SecurityUserDetails
        SecurityUserDetails userDetails = new SecurityUserDetails();
        userDetails.setUserId(accountId);
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(password);
        userDetails.setNickName(nickName);
        userDetails.setAccountNonLocked(accountNonLocked);//账号没有锁定
        userDetails.setAccountNonExpired(accountNonExpired);//账号没有过期
        userDetails.setCredentialsNonExpired(credentialsNonExpired);//账号证书没有过期
        userDetails.setEnabled(enabled);//账号启用
        return userDetails;
    }

//    private boolean isMobile(String v) {
//        Pattern pattern = Pattern.compile("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");
//        return pattern.matcher((v + "").trim()).matches();
//    }
//
//    private boolean isIdcard(String v) {
//        Pattern pattern = Pattern.compile("^(?=\\d{11}$)^1(?:3\\d|4[57]|5[^4\\D]|6[67]|7[^249\\D]|8\\d|9[189])\\d{8}$");
//        return pattern.matcher((v + "").trim()).matches();
//    }

}
