package com.zk.sgcc;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.springframework.util.Base64Utils;

/**
 * @author zhangkun
 */
public class Sm4Utils {

    public static final String KEY = "gdMfxJAPzBItGBxo724Byg==";

    public static String decrypt(String str) throws Exception {
        SymmetricCrypto sm4 = SmUtil.sm4(Base64Utils.decodeFromString(KEY));
        return sm4.decryptStr(str);
    }

    public static String encrypt(String str) throws Exception {
        SymmetricCrypto sm4 = SmUtil.sm4(Base64Utils.decodeFromString(KEY));
        return sm4.encryptHex(str);
    }

//    //生成一个加密字符串
//    public static void main(String[] args){
//        byte[] key = SmUtil.sm4().getSecretKey().getEncoded();
//        System.out.println(Base64Utils.encodeToString(key));
//    }

    public static void main(String[] args) {
        String str = "123456";
        try {
            byte[] key = SmUtil.sm4().getSecretKey().getEncoded();
            System.out.println(Base64Utils.encodeToString(key));
            String encodeStr = encrypt(str);
            System.out.println(encodeStr);
            System.out.println(decrypt(encodeStr));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
