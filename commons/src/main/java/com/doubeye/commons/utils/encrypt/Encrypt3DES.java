package com.doubeye.commons.utils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Encrypt3DES {

    private static final String ALGORITHM = "DESede";
    public static String encode(String plainText, String key) throws Exception {
        SecretKey desKey = new SecretKeySpec(prepareKey(key), ALGORITHM);
        Cipher c1 = Cipher.getInstance(ALGORITHM);
        c1.init(Cipher.ENCRYPT_MODE, desKey);
        return new String(Base64.getEncoder().encode(c1.doFinal(plainText.getBytes("utf-8"))));
    }

    public static String decode(String encryptedText, String key) throws Exception {
        SecretKey desKey = new SecretKeySpec(prepareKey(key), ALGORITHM);
        Cipher c1 = Cipher.getInstance(ALGORITHM);
        c1.init(Cipher.DECRYPT_MODE, desKey);
        return new String(c1.doFinal(Base64.getDecoder().decode(encryptedText)));
    }

    private static byte[] prepareKey(String key) throws NoSuchAlgorithmException {
        MessageDigest alg = MessageDigest.getInstance("MD5");
        alg.update(key.getBytes());
        byte[] keyBytes = alg.digest();
        int start = keyBytes.length;
        byte[] keyBytes24 = new byte[24];
        System.arraycopy(keyBytes, 0, keyBytes24, 0, start);
        System.arraycopy(keyBytes, 0, keyBytes24, start, 24 - start);
        return keyBytes24;
    }

    public static void main(String[] args) throws Exception {
        String encrypted = Encrypt3DES.encode("中文测试", "com.doubeye");
        System.out.println(encrypted);
        String plain = Encrypt3DES.decode(encrypted, "com.doubeye");
        System.out.println(plain);
    }
}
