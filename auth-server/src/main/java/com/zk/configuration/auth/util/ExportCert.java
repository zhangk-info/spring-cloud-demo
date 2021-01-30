package com.zk.configuration.auth.util;


import org.springframework.util.Base64Utils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.*;
import java.security.cert.Certificate;

/**
 * RSA 公钥私钥工具类
 * //        进入[%JAVA_HOME%]路径下
 * //        生成JKS Java KeyStore文件
 * //        keytool -genkeypair -alias mytest -keyalg RSA  -keypass mypass -keystore mytest.jks -storepass mypass
 * 本类用于通过jks文件获取公钥私钥及验证公钥私钥可用性
 */
public class ExportCert {

    //导出证书 base64格式
    public static void exportCert(KeyStore keyStore, String alias, String exportFile) throws Exception {
        Certificate certificate = keyStore.getCertificate(alias);
        String encoded = Base64Utils.encodeToString(certificate.getEncoded());
        FileWriter fw = new FileWriter(exportFile);
        fw.write("------Begin Certificate----- \r\n ");//非必须
        fw.write(encoded);
        fw.write("\r\n-----End Certificate-----");//非必须
        fw.close();
    }

    //得到KeyPair
    public static KeyPair getKeyPair(KeyStore keyStore, String alias, char[] password) {
        try {
            Key key = keyStore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                Certificate certificate = keyStore.getCertificate(alias);
                PublicKey publicKey = certificate.getPublicKey();
                return new KeyPair(publicKey, (PrivateKey) key);
            }
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    //导出私钥
    public static void exportPrivateKey(PrivateKey privateKey, String exportFile) throws Exception {
        String encoded = Base64Utils.encodeToString(privateKey.getEncoded());
        FileWriter fileWriter = new FileWriter(exportFile);
        fileWriter.write("-----Begin Private Key-----\r\n");//非必须
        fileWriter.write(encoded);
        fileWriter.write("\r\n-----End Private Key-----");//非必须
        fileWriter.close();
    }

    //导出公钥
    public static void exportPublicKey(PublicKey publicKey, String exportFile) throws Exception {
        String encoded = Base64Utils.encodeToString(publicKey.getEncoded());
        FileWriter fileWriter = new FileWriter(exportFile);
        fileWriter.write("-----BEGIN PUBLIC KEY-----\r\n");//非必须
        fileWriter.write(encoded);
        fileWriter.write("\r\n-----END PUBLIC KEY-----");//非必须
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        String keyStoreType = "jks";
        String keystoreFile = "D:\\jks\\mytest.jks";
        String storepass = "mypass"; //keystore的解析密码
        String keypass = "mypass";//条目的解析密码

        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(new FileInputStream(keystoreFile), storepass.toCharArray());

        String alias = "mytest";//条目别名
        String exportCertFile = "D:\\jks\\cert.txt";
        String exportPrivateFile = "D:\\jks\\privateKey.txt";
        String exportPublicFile = "D:\\jks\\publicKey.txt";

        ExportCert.exportCert(keyStore, alias, exportCertFile);
        KeyPair keyPair = ExportCert.getKeyPair(keyStore, alias, keypass.toCharArray()); //注意这里的密码是你的别名对应的密码，不指定的话就是你的keystore的解析密码
        ExportCert.exportPrivateKey(keyPair.getPrivate(), exportPrivateFile);
        ExportCert.exportPublicKey(keyPair.getPublic(), exportPublicFile);

        System.out.println("OK");

    }


}