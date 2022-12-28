package com.zk.configuration.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * “算法密钥”的工具类，用于生成“非对称加密算法的公钥和私钥”。
 *
 * @since 1.0
 */
public class AlgorithmKeyUtils {

    private AlgorithmKeyUtils() {
    }

    /**
     * 根据指定的“字节数组”生成“ECDSA”算法的公钥。
     *
     * @param bytes 字节数组
     * @return “ECDSA”算法的公钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  InvalidKeySpecException
     * @throws NoSuchProviderException  NoSuchProviderException
     */
    public static ECPublicKey generateEcPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return (ECPublicKey) getEcKeyFactoryInstance().generatePublic(new X509EncodedKeySpec(bytes));
    }

    /**
     * 根据指定的“字节数组”生成“ECDSA”算法的私钥。
     *
     * @param bytes 字节数组
     * @return “ECDSA”算法的私钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  InvalidKeySpecException
     * @throws NoSuchProviderException  NoSuchProviderException
     */
    public static ECPrivateKey generateEcPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return (ECPrivateKey) getEcKeyFactoryInstance().generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    /**
     * 根据指定的“字节数组”生成“RSA”算法的公钥。
     *
     * @param bytes 字节数组
     * @return “RSA”算法的公钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  InvalidKeySpecException
     */
    public static RSAPublicKey generateRsaPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (RSAPublicKey) getRsaKeyFactoryInstance().generatePublic(new X509EncodedKeySpec(bytes));
    }

    /**
     * 根据指定的“字节数组”生成“RSA”算法的私钥。
     *
     * @param bytes 字节数组
     * @return “RSA”算法的私钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  InvalidKeySpecException
     */
    public static RSAPrivateKey generateRsaPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (RSAPrivateKey) getRsaKeyFactoryInstance().generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    /**
     * 获得“RSA”算法的{@link KeyFactory}实例。
     *
     * @return “RSA”算法的{@link KeyFactory}实例
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    private static KeyFactory getRsaKeyFactoryInstance() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }

    /**
     * 获得“ECDSA”算法的{@link KeyFactory}实例。
     *
     * @return “ECDSA”算法的{@link KeyFactory}实例
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws NoSuchProviderException  NoSuchProviderException
     */
    private static KeyFactory getEcKeyFactoryInstance() throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        return KeyFactory.getInstance("ECDSA", "BC");
    }
}