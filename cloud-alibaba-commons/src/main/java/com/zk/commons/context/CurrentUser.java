package com.zk.commons.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUser {
    private Long userId;
    private String name;
    private String username;
    /**
     * 认证客户端ID
     */
    private String clientId;
    /**
     * 认证中心域,适用于区分多用户源,多认证中心域
     * 没用到
     */
    private String domain;


    //自定义属性
    private String nickName;
    private String userType;
}
