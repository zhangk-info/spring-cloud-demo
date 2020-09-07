package com.zk.commons.password;

import com.zk.sgcc.Sm2Utils;
import com.zk.sgcc.Sm4Utils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Security;
import java.util.Objects;

/**
 * SM4加密
 */
@Slf4j
public class SM4PasswordEncoder implements PasswordEncoder {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Sm2Utils.encrypt("123456"));
//        System.out.println(Sm4Utils.decrypt("A57364A1E93435A6E3AC0ED55199E9EB"));
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword != null) {
            try {
                return Sm4Utils.encrypt(rawPassword.toString());
            } catch (Exception e) {
                log.error("加密失败");
                return rawPassword.toString();
            }
        }
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null)
            return false;
        try {
            return /*Objects.equals(rawPassword, encodedPassword)
                    ||*/ Objects.equals(encode(Sm2Utils.decrypt(rawPassword.toString())), encodedPassword)/*sm2解密再sm4加密之后和密码对比*/
                    || Objects.equals(Sm2Utils.decrypt(rawPassword.toString()), encodedPassword);/*sm2解密之后同密码对比，用于oauth2的password模式的basic认证*/
        } catch (Exception e) {
            e.printStackTrace();
            log.error("密码对比错误");
            return false;
        }
    }

}
