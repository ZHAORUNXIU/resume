package com.x.resume.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Signature {

    static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    static final ZoneId ZONE_GMT = ZoneId.of("Z");
    static final String SIGN_METHOD = "HmacSHA256";
    static final String SIGN_VERSION = "2";

    private final String appKey;
    private final SecretKeySpec appSecretKey;

    public Signature(String appKey, String secretKey) {
        this.appKey = Objects.requireNonNull(appKey);
        this.appSecretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SIGN_METHOD);
    }

    private static final Logger log = LoggerFactory.getLogger(Signature.class);

    public Map<String, String> create(
            String method, String host, String encodedPath, Map<String, ?> params) {

        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(encodedPath).append('\n'); // /path

        // build signature:
        Map<String, String> result = new HashMap<>(5);
        result.put("AWSAccessKeyId", appKey);
        result.put("SignatureVersion", SIGN_VERSION);
        result.put("SignatureMethod", SIGN_METHOD);
        result.put("Timestamp", gmtNow());

        SortedMap<String, Object> sortedMap = new TreeMap<>(params);
        sortedMap.putAll(result);
        sortedMap.remove("Signature");

        for (Map.Entry<String, ?> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                sb.append(key).append('=').append(urlEncode(value.toString())).append('&');
            }
        }

        // remove last '&':
        sb.setLength(sb.length() - 1);

        // sign:
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance(SIGN_METHOD);
            hmacSha256.init(appSecretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        String actualSign = Base64.getEncoder().encodeToString(hash);
        result.put("Signature", actualSign);

        log.debug("signature={}, params={}", actualSign, params);
        return result;
    }

    private static String gmtNow() {
        return Instant.now().atZone(ZONE_GMT).format(DT_FORMAT);
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

}

