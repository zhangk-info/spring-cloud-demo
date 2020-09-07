package com.zk.sgcc;

import cn.hutool.core.util.RandomUtil;
import com.sgitg.sgcc.sm.SM4Utils;

import javax.crypto.KeyGenerator;

public class Sm4Utils<main> {

    public static final String KEY = "931531da6b124257b4bbbd0d2bf5fa74";

    public static String decrypt(String str) throws Exception {
        SM4Utils sm4 = new SM4Utils();
        byte[] sk = SgGmUtils.hexStringToBytes(KEY);
        byte[] cipherText = SgGmUtils.hexStringToBytes(str);
        byte[] plain = sm4.SG_DecECBData(sk, cipherText);
        return SgGmUtils.byteToString(plain);
    }

    public static String encrypt(String str) throws Exception {
        SM4Utils sm4 = new SM4Utils();
        byte[] gk = SgGmUtils.hexStringToBytes(KEY);
        byte[] cipherText = str.getBytes();
        byte[] plain = sm4.SG_EncECBData(gk, cipherText);
        return SgGmUtils.byteToHex(plain);
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
