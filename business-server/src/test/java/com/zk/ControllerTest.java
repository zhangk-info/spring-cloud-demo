package com.zk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by taosq on 2019/6/19
 *
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BusinessApplication.class})
@WebAppConfiguration
public class ControllerTest {
    protected MockMvc mvc;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() throws Exception {
        //使用 WebApplicationContext 构建 MockMvc
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    public void execute(RequestTypeEnum type, String url, Object obj) {
        Map<String, String> ps = new HashMap<>();
        if (obj != null) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(obj));
            for (String key : jsonObject.keySet()) {
                ps.put(key, jsonObject.getString(key));
            }
        }
        execute(type, url, ps);
    }

    public void execute(RequestTypeEnum type, String url, Map<String, String> params) {
        MockHttpServletRequestBuilder requestBuilder = null;
        switch (type) {
            case GET:
                requestBuilder = MockMvcRequestBuilders.get(url);
                break;
            case POST:
                requestBuilder = MockMvcRequestBuilders.post(url);
                break;
            case DELETE:
                requestBuilder = MockMvcRequestBuilders.delete(url);
                break;
            case PUT:
                requestBuilder = MockMvcRequestBuilders.put(url);
                break;
        }
        try {
            requestBuilder.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    requestBuilder.param(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
                }
            }

            requestBuilder.accept(MediaType.APPLICATION_JSON_UTF8_VALUE);

            ResultActions result = mvc.perform(requestBuilder);


            MvcResult mvcResult = result.andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();// 返回执行请求的结果

            System.out.println(mvcResult.getResponse().getContentAsString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
