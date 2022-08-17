package com.zk.configuration.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zk.commons.context.UserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommonMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("gmtCreate", new Date(),metaObject);
        this.setFieldValByName("createBy", UserContext.getId(),metaObject);
        this.setFieldValByName("createUser", getCurrentUser(),metaObject);
        //set update field
        this.updateFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtUpdate", new Date(),metaObject);
        this.setFieldValByName("updateBy", UserContext.getId(),metaObject);
        this.setFieldValByName("updateUser", getCurrentUser(),metaObject);
    }

    private String getCurrentUser() {
        return String.format("%s(%s)",UserContext.getUsername(),UserContext.getName());
    }

}
