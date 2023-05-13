package com.x.resume.common.util;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

import org.springframework.core.io.Resource;

/**
 * RSA加解密
 */
public class RSAKeyPair {

    /**
     * 己方私钥
     */
    public final PrivateKey privateKey;

    /**
     * 对方公钥
     */
    public final PublicKey publicKey;

    public RSAKeyPair(Resource privateKey, Resource publicKey) {
        this.privateKey = CipherUtils.readRSAPrivateKey(privateKey);
        this.publicKey = CipherUtils.readRSAPublicKey(publicKey);
    }

    public RSAKeyPair(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = Objects.requireNonNull(privateKey);
        this.publicKey = Objects.requireNonNull(publicKey);
    }

    public String decrypt(String data) {
        try {
            int pos = data.indexOf('\n');
            String encKey = data.substring(0, pos);

            byte[] aesKey = CipherUtils.decryptRSA(privateKey, CipherUtils.decodeBase64(encKey));
            if (aesKey.length != 32) {
                throw new IllegalArgumentException("invalid key size: " + aesKey.length);
            }

            pos = data.indexOf('\n', encKey.length() + 1);
            String encIv = data.substring(encKey.length() + 1, pos);

            byte[] aesIv = CipherUtils.decryptRSA(privateKey, CipherUtils.decodeBase64(encIv));
            if (aesIv.length != 16) {
                throw new IllegalArgumentException("invalid iv size: " + aesIv.length);
            }

            byte[] message = CipherUtils.decodeBase64(data.substring(pos + 1));
            byte[] decrypted = CipherUtils.decryptAES(aesKey, aesIv, message);

            return new String(decrypted);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String encrypt(String data) {
        try {
            byte[] aseKey = CipherUtils.randomBytes(32);
            byte[] aesIv = CipherUtils.randomBytes(16);
            byte[] message = data.getBytes(StandardCharsets.UTF_8);

            // 加密
            message = CipherUtils.encryptAES(aseKey, aesIv, message);
            aseKey = CipherUtils.encryptRSA(publicKey, aseKey);
            aesIv = CipherUtils.encryptRSA(publicKey, aesIv);

            // Base64
            String encKey = CipherUtils.encodeBase64(aseKey);
            String encIv = CipherUtils.encodeBase64(aesIv);
            String encMessage = CipherUtils.encodeBase64(message);

            StringBuilder sb = new StringBuilder(encMessage.length() + encKey.length() * 2 + 3);
            sb.append(encKey).append('\n')
                    .append(encIv).append('\n')
                    .append(encMessage).append('\n');
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
