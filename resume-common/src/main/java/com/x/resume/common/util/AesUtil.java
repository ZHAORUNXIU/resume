package com.x.resume.common.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密。算法：AES256/CBC/PKCS5Padding
 *
 * @author runxiu.zhao
 */
public class AesUtil {

    static final String CIPHER_NAME = "AES/CBC/PKCS5Padding";
    static final SecureRandom secureRandom;

    static {
        secureRandom = new SecureRandom();
    }

    /**
     * AES加密
     *
     * @param key AES Key，32字节
     * @param iv AES IV，16字节
     * @param input 明文输入
     * @return 加密后输出
     */
    public static byte[] encrypt(byte[] key, byte[] iv, byte[] input)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        // CBC模式需要生成一个16 bytes的initialization vector:
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
        return cipher.doFinal(input);
    }

    /**
     * AES解密
     *
     * @param key AES Key，32字节
     * @param iv AES IV，16字节
     * @param input 密文输入
     * @return 解密后输出
     * @throws GeneralSecurityException 解密失败
     */
    public static byte[] decrypt(byte[] key, byte[] iv, byte[] input)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        // CBC模式需要生成一个16 bytes的initialization vector:
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
        return cipher.doFinal(input);
    }

    /**
     * 创建32字节的随机Key
     *
     * @return Key
     */
    public static byte[] randomKey() {
        return randomBytes(32);
    }

    /**
     * 创建16字节的随机Key
     *
     * @return IV
     */
    public static byte[] randomIV() {
        return randomBytes(16);
    }

    static byte[] randomBytes(int size) {
        byte[] buffer = new byte[size];
        secureRandom.nextBytes(buffer);
        return buffer;
    }
}

