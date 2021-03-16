package com.zk.sgcc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * sm2加密
 * 使用hutool
 * Hutool针对Bouncy Castle做了简化包装，用于实现国密算法中的SM2、SM3、SM4。
 * BCUtil
 */
@Slf4j
public class Sm2Utils {

    public static final String PUBLIC_KEY = "0206fa3c15ce89e7201fcb2066d0f55e1a8a1b4c52ec950401026773d0343bd85f";
    public static final String PRIVATE_KEY = "3b4c64a9dcc33a0f9bb590e189966c4b65429c25f2b798933e6073b075ebf78e";


    /**
     * 解密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String decrypt(String str) throws Exception {
        ECPrivateKeyParameters privateKeyParams = BCUtil.toSm2Params(PRIVATE_KEY);
        SM2 sm2 = new SM2(privateKeyParams, null);
        SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
        sm2.setMode(mode);
        return sm2.decryptStr(str, KeyType.PrivateKey);
    }

    /**
     * 加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String encrypt(String str) throws Exception {
        //提取公钥点
        PublicKey publicKey = BCUtil.decodeECPoint(PUBLIC_KEY, "sm2p256v1");
        SM2 sm2 = new SM2(null, publicKey);
        SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
        sm2.setMode(mode);
        return sm2.encryptHex(str, KeyType.PublicKey);
    }

    public static void main(String[] args) {
        try {
            System.out.println(encrypt("zhangk..123"));
            System.out.println(Sm2Utils.decrypt("0449b75319c387d5b7a1137d61284ed99852c2f9a25e6d5129ddd0c5e8abf45a205c428976febc99acff9843d1f84752e2815b847d8ec1e95c53ff0b0d19fe40cf9c38882fff380feb5306d35bb8e58c3acf7d3bcb8e108b53584cf1bf54350f350cb8cc80e7793bffe706055bf432a6a33beb80769958"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    /**
     * 测试 这里包含生成一个新的public_key 和 private_key
     */
    public void test() {
        System.out.println("==========SM2 加解密测试 开始==========");
        String text = "我是一段测试aaaa";

        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();

        //公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
        String pubKey = Hex.toHexString(BCUtil.encodeECPublicKey(pair.getPublic()));
        String priKey = Hex.toHexString(BCUtil.encodeECPrivateKey(pair.getPrivate()));

        System.out.println("公钥：" + pubKey);
        System.out.println("私钥：" + priKey);

        SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
        SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
        sm2.setMode(mode);
        // 公钥加密，私钥解密
        String encryptStr = sm2.encryptHex(text, KeyType.PublicKey);
        System.out.println("密文：" + encryptStr);
        String decryptStr = StrUtil.utf8Str(sm2.decryptStr(encryptStr, KeyType.PrivateKey));
        System.out.println("明文：" + decryptStr);

        System.out.println("==========SM2 加解密测试 结束==========");
    }

}
