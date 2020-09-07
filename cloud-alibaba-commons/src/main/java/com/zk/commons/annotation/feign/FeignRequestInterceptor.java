package com.zk.commons.annotation.feign;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {
    /**
     * 微服务之间传递的唯一标识
     */
    private static final String X_REQUEST_ID = "X-RPC-Request-Id";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void apply(RequestTemplate template) {
        log.info("RequestTemplate -- transferHeader -- before: {}.", JSON.toJSONString(template));
        transferHeader(template);
        log.info("RequestTemplate -- transferHeader -- after: {}.", JSON.toJSONString(template));
        Request.Body body = template.requestBody();
        String bodyString = body.asString();
        log.info("request params is : {}.", bodyString);
        if (null != body && Objects.equals("GET", template.method()) && isJsonString(bodyString)) {
            transferBody(template, bodyString);
        }
        log.info("RequestTemplate -- transferBody -- after: {}.", JSON.toJSONString(template));
    }

    private boolean isJsonString(String bodyString) {
        return Objects.nonNull(bodyString) && bodyString.startsWith("{");
    }

    private void transferBody(RequestTemplate template, String bodyString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(bodyString);
            template.body(Request.Body.empty());
            Map<String, Collection<String>> queries = new HashMap<>();
            buildQuery(jsonNode, "", queries);
            template.queries(queries);
        } catch (IOException e) {
            log.error("feign call transfer body error.", e);
        }
    }

    private void buildQuery(JsonNode jsonNode, String path, Map<String, Collection<String>> queries) {
        if (!jsonNode.isContainerNode()) { // 叶子节点
            if (jsonNode.isNull()) {
                return;
            }
            Collection<String> values = queries.get(path);
            if (null == values) {
                values = new ArrayList<>();
                queries.put(path, values);
            }
            values.add(jsonNode.asText());
            return;
        }
        if (jsonNode.isArray()) { // 数组节点
            Iterator<JsonNode> it = jsonNode.elements();
            while (it.hasNext()) {
                buildQuery(it.next(), path, queries);
            }
        } else {
            Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (StringUtils.hasText(path)) {
                    buildQuery(entry.getValue(), path + "." + entry.getKey(), queries);
                } else { // 根节点
                    buildQuery(entry.getValue(), entry.getKey(), queries);
                }
            }
        }
    }

    private void transferHeader(RequestTemplate template) {
        HttpServletRequest httpServletRequest = getHttpServletRequest();

        try {
            log.error("httpServletRequest -- " + httpServletRequest);
            Map<String, String> headers = getHeaders(httpServletRequest);
            log.error("headers -- " + JSON.toJSONString(headers));
        } catch (Exception e) {

        }

        if (httpServletRequest != null) {
            Map<String, String> headers = getHeaders(httpServletRequest);
            // 传递所有请求头,防止部分丢失
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                //去掉一个头
                if (!entry.getKey().equals("transfer-encoding")) {
                    template.header(entry.getKey(), entry.getValue());
                }
            }
            // 微服务之间传递的唯一标识
            if (!headers.containsKey(X_REQUEST_ID)) {
                String sid = String.valueOf(UUID.randomUUID());
                template.header(X_REQUEST_ID, sid);
            }
            log.debug("FeignRequestInterceptor -- RequestTemplate:{}", JSON.toJSONString(template));
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }
}
