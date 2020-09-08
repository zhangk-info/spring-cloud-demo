package com.zk.business;

import com.alibaba.fastjson.JSONObject;
import com.zk.ControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;

@Slf4j
public class BusinessTest extends ControllerTest {
    @Resource
    ApplicationContext ac;

    @Test
    public void testContainer() {

        JwtAccessTokenConverter t = ac.getBean(JwtAccessTokenConverter.class);
        log.error(JSONObject.toJSONString(t.getKey()));
    }
}
