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
    private Long accountId;
    private Integer userType;
}
