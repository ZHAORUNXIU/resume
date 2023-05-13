package com.x.resume.common.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对Token进行加解密
 *
 * @author runxiu.zhao
 */
public class TokenEncryptUtil {

    static final String CIPHER_NAME = "AES/ECB/PKCS5Padding";

    static final byte[] KEY = generateKey();

    static byte[] generateKey() {
        // IMPORTANT: 不要修改创建Key的代码:
        final long SEED = 0x3e8a93b243c89d12L;
        final long multiplier = 0x5DEECE66DL;
        final long addend = 0xBL;
        final long mask = (1L << 48) - 1;
        long seed = SEED;
        byte[] buffer = new byte[16];
        for (int i = 0; i < 16; i++) {
            seed = (seed * multiplier + addend) & mask;
            buffer[i] = (byte) seed;
        }
        return buffer;
    }

    public static String encryptToken(String plain) throws GeneralSecurityException {
        byte[] enc = null;
        try {
            enc = encrypt(plain.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException();
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(enc);
    }

    static final Pattern ORIGIN_TOKEN = Pattern.compile("^default_([a-f0-9]{32})$");

    public static String decryptToken(String encrypted) throws GeneralSecurityException {
        Matcher m = ORIGIN_TOKEN.matcher(encrypted);
        if (m.matches()) {
            return m.group(1);
        }

        byte[] enc = null;
        try {
            enc = Base64.getUrlDecoder().decode(encrypted);
            byte[] dec = decrypt(enc);
            return new String(dec, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException();
        }
    }

    // 加密:
    static byte[] encrypt(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    // 解密:
    static byte[] decrypt(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

}

