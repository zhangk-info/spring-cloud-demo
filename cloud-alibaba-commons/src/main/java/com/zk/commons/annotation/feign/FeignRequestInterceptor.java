package com.zk.commons.annotation.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request.Body;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class FeignRequestInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(FeignRequestInterceptor.class);
    private static final String X_REQUEST_ID = "X-RPC-Request-Id";
    @Autowired
    private ObjectMapper objectMapper;

    public FeignRequestInterceptor() {
    }

    public void apply(RequestTemplate template) {
        this.transferHeader(template);
        Body body = template.requestBody();
        String bodyString = body.asString();
        log.info("request params is : {}.", bodyString);
        if (null != body && Objects.equals("GET", template.method()) && this.isJsonString(bodyString)) {
            this.transferBody(template, bodyString);
        }

    }

    private boolean isJsonString(String bodyString) {
        return Objects.nonNull(bodyString) && bodyString.startsWith("{");
    }

    private void transferBody(RequestTemplate template, String bodyString) {
        try {
            JsonNode jsonNode = this.objectMapper.readTree(bodyString);
            template.body(Body.empty());
            Map<String, Collection<String>> queries = new HashMap();
            this.buildQuery(jsonNode, "", queries);
            template.queries(queries);
        } catch (IOException var5) {
            log.error("feign call transfer body error.", var5);
        }

    }

    private void buildQuery(JsonNode jsonNode, String path, Map<String, Collection<String>> queries) {
        if (!jsonNode.isContainerNode()) {
            if (!jsonNode.isNull()) {
                Collection<String> values = (Collection) queries.get(path);
                if (null == values) {
                    values = new ArrayList();
                    queries.put(path, values);
                }

                ((Collection) values).add(jsonNode.asText());
            }
        } else {
            Iterator it;
            if (jsonNode.isArray()) {
                it = jsonNode.elements();

                while (it.hasNext()) {
                    this.buildQuery((JsonNode) it.next(), path, queries);
                }
            } else {
                it = jsonNode.fields();

                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> entry = (Map.Entry) it.next();
                    if (StringUtils.hasText(path)) {
                        this.buildQuery((JsonNode) entry.getValue(), path + "." + (String) entry.getKey(), queries);
                    } else {
                        this.buildQuery((JsonNode) entry.getValue(), (String) entry.getKey(), queries);
                    }
                }
            }

        }
    }

    private void transferHeader(RequestTemplate template) {
        HttpServletRequest httpServletRequest = this.getHttpServletRequest();
        if (httpServletRequest != null) {
            Map<String, String> headers = this.getHeaders(httpServletRequest);
            Iterator iterator = headers.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                if (!Objects.equals("transfer-encoding", entry.getKey())) {
                    template.header((String) entry.getKey(), new String[]{(String) entry.getValue()});
                }
            }

            if (!headers.containsKey("X-RPC-Request-Id")) {
                String sid = String.valueOf(UUID.randomUUID());
                template.header("X-RPC-Request-Id", new String[]{sid});
            }

            log.error("FeignRequestInterceptor:{}", template.toString());
        }

    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception var2) {
            return null;
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }

        return map;
    }
}
