package com.zk.encrypted_transfer;

import com.zk.commons.exception.ServiceException;
import com.zk.sgcc.Sm2Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 密文传输
 * RequestBodyAdvice
 * 对于BODY类型加密传输的解密
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
 */
@ControllerAdvice(annotations = RestController.class)
@Conditional(Sm2EnableCondtion.class)
@Slf4j
public class MyRequestBodyAdvice implements RequestBodyAdvice {

    private String privateKeyA;
    private List<String> ignoreUris;

    public MyRequestBodyAdvice(String privateKeyA, List<String> ignoreUris) {
        this.privateKeyA = privateKeyA;
        this.ignoreUris = ignoreUris;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            AntPathMatcher matcher = new AntPathMatcher();
            // 过滤传入明文的uri
            if (!CollectionUtils.isEmpty(ignoreUris)) {
                for (String url : ignoreUris) {
                    if (matcher.match(url, request.getRequestURI())) {
                        return httpInputMessage;
                    }
                }
            }
        } catch (Exception e) {
            return httpInputMessage;
        }

        return new HttpInputMessage() {

            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                String enCodeStr = IOUtils.toString(httpInputMessage.getBody(), "UTF-8");

                // 解密前端的密文加上04
                enCodeStr = "04" + enCodeStr;
                String deCodeStr = "";
                //  解密
                try {
                    deCodeStr = deCodeStr = Sm2Utils.decrypt(enCodeStr, privateKeyA);
                } catch (Exception e) {
                    throw new ServiceException("解密失败");
                }

                return IOUtils.toInputStream(deCodeStr, "UTF-8");
            }
        };
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}