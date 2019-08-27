package com.beeasy.hzback;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.security.Security;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//public class zip {
//
//    /**
//     * 加密用的Key 可以用26个字母和数字组成
//     * 此处使用AES-128-CBC加密模式，key需要为16位。
//     */
//    private static String sKey = "lingyejunAesTest";
//    private static String ivParameter = "1234567890123456";
//
//    // 加密
//    public static String encrypt(String sSrc) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        byte[] raw = sKey.getBytes();
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
//        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
//    }
//
//    // 解密
//    public static String decrypt(String sSrc) {
//        try {
//            byte[] raw = sKey.getBytes("ASCII");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
//            byte[] original = cipher.doFinal(encrypted1);
//            String originalString = new String(original, "utf-8");
//            return originalString;
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public static void main(String[] args) {
//        String email = "lingyejun@java.aes";
//        try {
//            String sec = encrypt(email);
//            System.out.println(sec);
//            System.out.println(decrypt("CcOtM9WXv0N+Owh/xxedZJnuNUaTU7y3aUBESQLUvVM="));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

public class zip {

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";//默认的加密算法
    /**
     * AES 加密操作
     * @param content 待加密内容
     * @param password 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        try {
            SecretKeySpec skeySpec = getKey(password);
            byte[] clearText = content.getBytes("UTF8");
            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            //below code must be added in java end
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(clearText);
            return "";
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param password
     * @return
     */
    public static String decrypt(String content, String password) {
        try {
            SecretKey key = getKey(password);
            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encrypedPwdBytes = Base64.decodeBase64(content);
            //below code must be added in java end
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes , "UTF-8");
            return decrypedValue;
        } catch (Exception ex) {
            System.err.println("Decrypt exception: " + ex.getMessage());
        }
        return null;
    }

    private static SecretKeySpec getKey(String password) throws UnsupportedEncodingException {
        // You can change it to 128 if you wish
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        // explicitly fill with zeros
        Arrays.fill(keyBytes, (byte) 0x0);

        // if password is shorter then key length, it will be zero-padded
        // to key length
        byte[] passwordBytes = password.getBytes("UTF-8");
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    @Test
    public void test(){

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine se = manager.getEngineByName("js");
        String str = "13>12&&'张三1'=='张三'";
        boolean result;
        try {
            result = ( Boolean)se.eval(str);
            System.out.println(result);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        String res = decrypt(encrypt("sss", "sss"), "sss");
        System.out.println(res);

    }

    @Test
    public void readByZipFile() {
        String str = FileUtil.readUtf8String("D:\\java projects\\hznsh\\load-qcc\\src\\main\\resources\\qccLoadData\\txt\\201908255f7f730923ca4c2e8007186abc52ced6\\corp.json");
        JSONObject obj = JSONObject.parseObject(str);

//            new ZipFile("D:\\java projects\\hznsh\\load-qcc\\src\\main\\resources\\qccLoadData\\zip\\201908255f7f730923ca4c2e8007186abc52ced6.zip", "dl68LxN0aXRAQTfvf5Z6".toCharArray()).extractAll( "D:\\java projects\\hznsh\\load-qcc\\src\\main\\resources\\qccLoadData\\txt\\");

//            ZipFile zipFile = new ZipFile(new File("D:\\java projects\\hznsh\\load-qcc\\src\\main\\resources\\qccLoadData\\zip\\201908255f7f730923ca4c2e8007186abc52ced6.zip"));

    }

    public void readByZipInputStream() {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(
                    new FileInputStream("D:\\java projects\\hznsh\\load-qcc\\src\\main\\resources\\qccLoadData\\zip\\201908255f7f730923ca4c2e8007186abc52ced6.zip"));
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String name = zipEntry.getName();
                System.out.println(name);
                byte[] b = new byte[1024];
                zipInputStream.read(b);
                System.out.println(new String(b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}