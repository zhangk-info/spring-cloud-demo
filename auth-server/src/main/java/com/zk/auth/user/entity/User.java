package com.zk.auth.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
//这个设置为true生成的get/set方法没有get/set前缀
@Accessors(chain = true)
@TableName("zk_user")
public class User {
    @JsonSerialize(using = ToStringSerializer.class)// 精度丢失 方式1
    @JsonFormat(shape = JsonFormat.Shape.STRING) // 精度丢失 方式2
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 姓名
     */
    private String name;
    /**
     * 标识：手机号、邮箱、 用户名、或第三方应用的唯一标识
     */
    private String username;

    /**
     * 密码凭证：站内的保存密码、站外的不保存或保存token
     */
    @JsonIgnore
    private String password;

    /**
     * 用户类型
     */
    private Integer userType;

    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;
}
