package com.zk.sgcc;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class Sm4Utils<main> {

    public static final String KEY = "931531da6b124257b4bbbd0d2bf5fa74";

    public static String decrypt(String str) throws Exception {
        SymmetricCrypto sm4 = SmUtil.sm4();
        return sm4.decryptStr(str);
    }

    public static String encrypt(String str) throws Exception {
        SymmetricCrypto sm4 = SmUtil.sm4();
        return sm4.encryptHex(str);
    }

    //生成一个加密字符串
//    public static void main(String[] args){
//        System.out.println(RandomUtil.simpleUUID());
//    }

    public static void main(String[] args) {
        String str = "zhangk..123";
//        String str = "zhangk";
        try {
//            System.out.println(decrypt(str));
            System.out.println(encrypt(str));
        } catch (Exception e) {

        }

    }
}
