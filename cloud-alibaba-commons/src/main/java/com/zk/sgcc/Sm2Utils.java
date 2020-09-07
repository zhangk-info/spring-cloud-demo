package com.zk.sgcc;

import com.sgitg.sgcc.sm.SM2Utils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * sm2加密
 *
 */
public class Sm2Utils {

    public static final String PRIVATE_KEY = "641D88AF74A05D0EF4115D8CB5BDA277165577A52399BE2B123422C33E034E21";
    public static final String PUBLIC_KEY = "04A0773DFB250EB31E69E0D86DBBF1946D0AC196A5C7A51F267F5775C8771253AC70A15FD71ED6D3D74BB33BACB4C71D9BD18188587A999FA5A7ACCF6FA5544E27";

    /**
     * 解密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String decrypt(String str) throws Exception {
        SM2Utils sm2Utils = new SM2Utils();
        byte[] sk = SgGmUtils.hexStringToBytes(PRIVATE_KEY);
        byte[] cipherText = SgGmUtils.hexStringToBytes(str);
        byte[] plain = sm2Utils.SG_SM2DecData(sk, cipherText);
        return SgGmUtils.byteToString(plain);
    }

    /**
     * 加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String encrypt(String str) throws Exception {
        SM2Utils sm2Utils = new SM2Utils();
        byte[] gk = SgGmUtils.hexStringToBytes(PUBLIC_KEY);
        byte[] cipherText = str.getBytes();
        byte[] plain = sm2Utils.SG_SM2EncData(gk, cipherText);
        return SgGmUtils.byteToHex(plain);
    }

    public static void main(String[] args) {
        try {
            System.out.println(encrypt("zhangk..123"));
            System.out.println(Sm2Utils.decrypt("04B05751D951357D9EC569D8EA8BE4EE4687C3E1DF42AEEAE49A7A41EE930279345E2F3AEEFF7352544CDA14E3C7CD02DF25179D45B3D12B9444FAF82057DD9A7F3AB65BA4145C221F029D8C6D764F40EB8A6D53ED845AA4687901CA960294E605DCDE09DAE32DE94A269012"));
        } catch (Exception e) {

        }
    }

    @Test
    /**
     * 测试 这里包含生成一个新的public_key 和 private_key
     */
    public void test() {
        System.out.println("==========SM2 加解密测试 开始==========");
        byte[] sk = new byte[2048];
        byte[] pk = new byte[2048];
        byte[] key = new byte[2048];
        byte[] cipherText = new byte[2048];
        byte[] plain = new byte[2048];
        String strRnd = "xxxxxxxxx";

        SM2Utils sm2Utils = new SM2Utils();
        key = sm2Utils.SG_generateKeyPair();

        if (key != null) {
            // 加密数据
            System.out.println("加密数据：" + strRnd);
            // 通过解析生成的公钥私钥
            sk = Arrays.copyOfRange(key, 0, 32);
            pk = Arrays.copyOfRange(key, 32, 97);
            System.out.println("私钥=" + SgGmUtils.byteToHex(sk));
            System.out.println("公钥=" + SgGmUtils.byteToHex(pk));
            try {
                //对明文使用SM2进行加密
                cipherText = sm2Utils.SG_SM2EncData(pk, strRnd.getBytes());
                System.out.println("加密后：" + SgGmUtils.byteToHex(cipherText));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // 通过生成的公钥进行解密
            try {
                plain = sm2Utils.SG_SM2DecData(sk, cipherText);
                System.out.println("解密后数据为=" + SgGmUtils.byteToString(plain));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("==========SM2 加解密测试 结束==========");
    }

}
