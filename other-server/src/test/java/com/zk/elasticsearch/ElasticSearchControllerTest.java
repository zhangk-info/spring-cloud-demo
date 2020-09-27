package com.zk.elasticsearch;

import com.zk.ControllerTest;
import com.zk.RequestTypeEnum;
import com.zk.UtilApplication;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(classes = UtilApplication.class)
public class ElasticSearchControllerTest extends ControllerTest {

    private final String URI = "api/v1/";

    @Test
    public void save() throws Exception {
        Map<String, String> params = new HashMap<>();

        this.execute(RequestTypeEnum.PUT, URI + "batch", params);
    }

}