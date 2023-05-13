package com.x.resume.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

public class AES256Util {

    private final static Logger LOG = LoggerFactory.getLogger(AES256Util.class);

    /**
     * 初始向量IV, 初始向量IV的长度规定为128位16个字节, 初始向量的来源为随机生成.
     */
    private static final byte[] KEY_VI = "MdWxu4XWTTCf848e".getBytes();

    private static final String AES = "AES";

    /**
     * 加密解密算法/加密模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    static {
        Security.setProperty("crypto.policy", "unlimited");
    }

    /**
     * AES加密
     */
    public static String encode(String key, String content) {
        if (Text.isBlank(content)) {
            return Text.EMPTY;
        }
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(KEY_VI));
            byte[] byteEncode = content.getBytes(StandardCharsets.UTF_8);
            byte[] byteAES = cipher.doFinal(byteEncode);
            return BASE64_ENCODER.encodeToString(byteAES);
        } catch (IllegalBlockSizeException
                | BadPaddingException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException ex) {
            LOG.error(Log.format("AES256加密失败", Log.kv("content", content)), ex);
        }
        return Text.EMPTY;
    }

    public static byte[] encode(String key, byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(KEY_VI));
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException
                | BadPaddingException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException ex) {
            LOG.error(Log.format("AES256加密失败"), ex);
        }
        return null;
    }

    /**
     * AES解密
     */
    public static String decode(String key, String content) {
        if (Text.isBlank(content)) {
            return Text.EMPTY;
        }
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(KEY_VI));
            byte[] byteContent = BASE64_DECODER.decode(content);
            byte[] byteDecode = cipher.doFinal(byteContent);
            return new String(byteDecode, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException
                | BadPaddingException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException ex) {
            LOG.error(Log.format("AES256解密失败", Log.kv("content", content)), ex);
        }
        return Text.EMPTY;
    }

    public static byte[] decode(String key, byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(KEY_VI));
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException
                | BadPaddingException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException ex) {
            LOG.error(Log.format("AES256解密失败"), ex);
        }
        return null;
    }
}
