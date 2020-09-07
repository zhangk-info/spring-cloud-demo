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
        this.setInsertFieldValByName("gmtCreate", new Date(),metaObject);
        this.setInsertFieldValByName("createBy", UserContext.getId(),metaObject);
        this.setInsertFieldValByName("createUser", getCurrentUser(),metaObject);
        //set update field
        this.updateFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setInsertFieldValByName("gmtUpdate", new Date(),metaObject);
        this.setInsertFieldValByName("updateBy", UserContext.getId(),metaObject);
        this.setInsertFieldValByName("updateUser", getCurrentUser(),metaObject);
    }

    private String getCurrentUser() {
        return String.format("%s(%s)",UserContext.getUsername(),UserContext.getName());
    }

}
