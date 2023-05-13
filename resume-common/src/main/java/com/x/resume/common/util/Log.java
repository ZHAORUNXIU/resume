package com.x.resume.common.util;

import com.x.resume.common.model.KeyValue;
import com.x.resume.common.model.Result;

import java.util.HashMap;
import java.util.Map;

public class Log {

    private static ThreadLocal<Log> local = ThreadLocal.withInitial(Log::new);

    private String accessId;
    private String ip;
    private String exchangeId;
    private Map<String, String> params = new HashMap<>();

    public static String format(int code, String message, KeyValue... kvs) {
        StringBuilder buf = new StringBuilder(200);

        if (code != 0) {
            buf.append("code:").append(code).append('\t');
        }

        if (Text.isNotBlank(message)) {
            buf.append("msg:").append(message).append('\t');
        }

        if (kvs != null) {
            for (KeyValue kv : kvs) {
                buf.append(kv.getKey()).append(':').append(kv.getValue()).append('\t');
            }
        }

        Log log = local.get();
        if (Text.isNotBlank(log.getAccessId())) {
            buf.append("uuid:").append(log.getAccessId()).append('\t');
        }

        if (Text.isNotBlank(log.getIp())) {
            buf.append("ip:").append(log.getIp()).append('\t');
        }

        if (Text.isNotBlank(log.getExchangeId())) {
            buf.append("exchangeId:").append(log.getExchangeId()).append('\t');
        }

        Map<String, String> localParams = log.getParams();
        if (!localParams.isEmpty()) {
            localParams.forEach((k, v) -> {
                if (Text.isNotBlank(k) && Text.isNotBlank(v)) {
                    buf.append(k).append(':').append(v).append('\t');
                }
            });
        }

        int len = buf.length();
        if (len > 0) {
            char ch = buf.charAt(len - 1);
            if (ch == ' ' || ch == '\t') {
                buf.deleteCharAt(len - 1);
            }
        }

        return buf.length() == 0 ? "" : buf.toString();
    }

    public static String format(String message, KeyValue... kvs) {
        return format(0, message, kvs);
    }

    public static String format(Result result, KeyValue... kvs) {
        return format(result.getCode(), result.getMessage(), kvs);
    }

    public static KeyValue kv(String key, Object value) {
        return new KeyValue(key, value);
    }

    public static Log get() {
        return local.get();
    }

    public static void remove() {
        local.remove();
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            this.params.putAll(params);
        }
    }

    public void setParam(String key, String value) {
        if (Text.isNotBlank(key) && Text.isNotBlank(value)) {
            params.put(key, value);
        }
    }

    public String removeParam(String key) {
        return params.remove(key);
    }

    public String getParam(String key) {
        return params.get(key);
    }

}
