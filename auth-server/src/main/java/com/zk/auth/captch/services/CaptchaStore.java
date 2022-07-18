package com.zk.auth.captch.services;

import com.zk.auth.captch.cache.DefaultCache;
import com.zk.auth.captch.cache.ICache;
import com.zk.auth.captch.exception.ImageCodeException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CaptchaStore implements ImageCodeStore {

    private ICache<String, String> captchaMap = new DefaultCache<>(3000000);

    public void set(String key, String code) {
        captchaMap.put(key, code);
    }

    public void validated(String signature, String code) {
        if (!captchaMap.containsKey(signature)) {
            throw new ImageCodeException(401001,"签名错误，请刷新页面重新获取。");
        }

        String orignalCode = captchaMap.get(signature);
        if (StringUtils.isBlank(orignalCode)) {
            throw new ImageCodeException(401002,"验证码过期了，请刷新页面重新获取。");
        }

        if (!orignalCode.equalsIgnoreCase(code)) {
            throw new ImageCodeException(401003,"验证码错误，请重新输入。");
        }
    }

    @Override
    public boolean hasSignature(String signature) {
        return captchaMap.containsKey(signature);
    }

    @Override
    public void remove(String signature) {
        captchaMap.remove(signature);
    }
}
