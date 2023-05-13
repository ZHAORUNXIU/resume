package com.x.resume.common.util;

import com.alibaba.fastjson.JSON;
import com.x.resume.common.exception.JsonException;

public class SerializationUtil {

    public static <T> T deserialize(String data, Class<T> clazz) throws JsonException {
        if (clazz == String.class) {
            return (T) data;
        }
        return JSON.parseObject(data, clazz);
    }

    public static String serialize(Object data) throws JsonException {
        if (data instanceof String) {
            return (String) data;
        }

        return JSON.toJSONString(data);
    }

}
