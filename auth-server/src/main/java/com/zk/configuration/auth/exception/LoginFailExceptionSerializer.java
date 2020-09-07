package com.zk.configuration.auth.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class LoginFailExceptionSerializer extends StdSerializer<LoginFailException> {

    protected LoginFailExceptionSerializer() {
        super(LoginFailException.class);
    }

    @Override
    public void serialize(LoginFailException e, JsonGenerator gen, SerializerProvider provider) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        gen.writeStartObject();
        Throwable realException = e.getRealException();
        String message = "";
        if (realException instanceof UnsupportedGrantTypeException) {
            gen.writeStringField("code", "401001");
            message = "授权类型错误";
        } else if (realException instanceof InvalidGrantException) {
            gen.writeStringField("code", "401002");
            message = "用户名或者密码错误";
        } else {
            gen.writeStringField("code", "401003");
            message = realException.getClass().getSimpleName() + "::" + e.getMessage();
        }

//        if (null != realException) {
//            gen.writeStringField("message", realException.getClass().getSimpleName() + "::" + e.getMessage());
//        } else {
//            gen.writeStringField("message", e.getMessage());
//        }
        gen.writeStringField("message", message);
        gen.writeStringField("path", request.getServletPath());
        gen.writeStringField("timestamp", String.valueOf(new Date().getTime()));
        if (e.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> entry : e.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                gen.writeStringField(key, add);
            }
        }
        gen.writeEndObject();
    }
}