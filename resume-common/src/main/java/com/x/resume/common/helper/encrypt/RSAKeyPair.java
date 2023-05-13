package com.x.resume.common.helper.encrypt;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * RSA加解密／签名
 *
 * @author runxiu.zhao
 */
public class RSAKeyPair {

    // 私钥:
    final PrivateKey privateKey;

    // 公钥:
    final PublicKey publicKey;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 读取Public Key和Private Key
     *
     * @param privateKey Encoded private key text
     * @param publicKey Encoded public key text
     */
    public RSAKeyPair(String privateKey, String publicKey) {
        this(new StringReader(privateKey), new StringReader(publicKey));
    }

    /**
     * 读取Public Key和Private Key
     *
     * @param privateKey Reader对象
     * @param publicKey Reader对象
     */
    public RSAKeyPair(Reader privateKey, Reader publicKey) {
        this.privateKey = readPrivateKey(privateKey);
        this.publicKey = readPublicKey(publicKey);
    }

    /**
     * 仅读取Public Key
     *
     * @param publicKey Encoded public key text
     */
    public RSAKeyPair(String publicKey) {
        this(new StringReader(publicKey));
    }

    /**
     * 仅读取Public Key
     *
     * @param publicKey Reader对象
     */
    public RSAKeyPair(Reader publicKey) {
        this.privateKey = null;
        this.publicKey = readPublicKey(publicKey);
    }

    static PrivateKey readPrivateKey(Reader reader) {
        try (PemReader preader = new PemReader(reader)) {
            PemObject po = preader.readPemObject();
            byte[] encoded = po.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    static PrivateKey readPrivateKey(String text) {
        return readPrivateKey(new StringReader(text));
    }

    static PublicKey readPublicKey(Reader reader) {
        try (PemReader preader = new PemReader(reader)) {
            PemObject po = preader.readPemObject();
            byte[] encoded = po.getContent();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    static PublicKey readPublicKey(String text) {
        return readPublicKey(new StringReader(text));
    }

    /**
     * 用私钥加密
     *
     * @param message 明文
     * @return 密文
     * @throws GeneralSecurityException 加密失败
     */
    public byte[] encryptByPrivateKey(byte[] message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
        return cipher.doFinal(message);
    }

    /**
     * 用私钥解密
     *
     * @param input 密文
     * @return 明文
     * @throws GeneralSecurityException 解密失败
     */
    public byte[] decryptByPrivateKey(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        return cipher.doFinal(input);
    }

    /**
     * 用公钥解密
     *
     * @param input 密文
     * @return 明文
     * @throws GeneralSecurityException 解密失败
     */
    public byte[] decryptByPublicKey(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.publicKey);
        return cipher.doFinal(input);
    }

    /**
     * 用公钥加密
     *
     * @param message 明文
     * @return 密文
     * @throws GeneralSecurityException 加密失败
     */
    public byte[] encryptByPublicKey(byte[] message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        return cipher.doFinal(message);
    }

    /**
     * 用私钥进行SHA1withRSA签名
     *
     * @param message 明文
     * @return 签名
     * @throws GeneralSecurityException 签名失败
     */
    public byte[] sign(byte[] message) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(this.privateKey);
        signature.update(message);
        return signature.sign();
    }

    /**
     * 用公钥验证SHA1withRSA签名
     *
     * @param message 明文
     * @param sign 签名
     * @return 签名是否有效，true=有效，false=无效
     * @throws GeneralSecurityException 验证失败
     */
    public boolean verify(byte[] message, byte[] sign) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(this.publicKey);
        signature.update(message);
        return signature.verify(sign);
    }

    public String getEncodedPublicKey() {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        String s = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        while (s.length() > 76) {
            sb.append(s.substring(0, 76)).append('\n');
            s = s.substring(76);
        }
        sb.append("-----END PUBLIC KEY-----");
        return sb.toString();
    }
}

