package com.zk.encrypted_transfer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class Sm2EnableCondtion implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String property = environment.getProperty("sm2.enable");
        if (StringUtils.isNotEmpty(property) && property.equals("true")){
            return true;
        }

        return false;
    }
}
