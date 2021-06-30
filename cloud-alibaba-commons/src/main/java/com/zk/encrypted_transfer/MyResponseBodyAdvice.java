package com.zk.encrypted_transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.commons.exception.ServiceException;
import com.zk.sgcc.Sm2Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * 密文传输
 * ResponseBodyAdvice
 * 将Controller返回参数进行加密传输
 * 服务中配置方法 {@see CipherConfig})
 * <p>
 * fix bug : @ControllerAdvice里面包含了@Component，所以该类会自动注入
 * 使用@Conditional(Sm2EnableCondtion.class)
 * <pre>
 * public class Sm2EnableCondtion implements Condition {
 *
 *     @Override
 *     public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
 *         Environment environment = context.getEnvironment();
 *         String property = environment.getProperty("sm2.enable");
 *         if (StringUtils.isNotEmpty(property) && property.equals("true")){
 *             return true;
 *         }
 *
 *         return false;
 *     }
 * }
 * </pre>
 * </p>
 */
@ControllerAdvice(annotations = RestController.class)
@Conditional(Sm2EnableCondtion.class)
@Slf4j
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    private String publicKeyB;

    private ObjectMapper objectMapper;

    private List<String> ignoreUris;

    public MyResponseBodyAdvice(String publicKeyB, ObjectMapper objectMapper, List<String> ignoreUris) {
        this.publicKeyB = publicKeyB;
        this.objectMapper = objectMapper;
        this.ignoreUris = ignoreUris;
    }

    /**
     * 选择哪些类，或哪些方法需要走beforeBodyWrite
     * 从arg0中可以获取方法名和类名
     * arg0.getMethod().getDeclaringClass().getName()为获取方法名
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!selectedContentType.getSubtype().equals("json")) {
            // 只有返回json数据流的时候才加密，其它比如文件下载不加密
            // vnd.openxmlformats-officedocument.spreadsheetml.sheet
            return body;
        }

        // 过滤返回明文的uri
//        AntPathMatcher matcher = new AntPathMatcher();
//        if (!CollectionUtils.isEmpty(ignoreUris)) {
//            for (String url : ignoreUris) {
//                if (matcher.match(url, request.getURI().getPath())) {
//                    return body;
//                }
//            }
//        }
        // 其他uri
        String out = null;
        try {
            out = objectMapper.writeValueAsString(body);
            // 加密
            out = Sm2Utils.encrypt(out, publicKeyB);
            // 返回给前端的密文去掉04
            out = out.substring(2, out.length());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("加密失败");
        }
        return out;
    }
}