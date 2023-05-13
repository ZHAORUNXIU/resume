package com.x.resume.types.common;

import org.springframework.util.StringUtils;

import java.util.Locale;

public enum LanguageType {
    /**
     * 韩文
     */
    ko_kR("ko_KR", 1),
    /**
     * 中文
     */
    zh_CN("zh_CN", 2),

    /**
     * 英文
     */
    en_US("en_US", 3);

    private String key;
    private Integer value;

    LanguageType(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public static LanguageType find(Locale locale) {
        return find(locale.toString());
    }

    public static LanguageType find(String key) {
        if (StringUtils.isEmpty(key)) {
            return ko_kR;
        }
        for (LanguageType language : LanguageType.values()) {
            if (key.equalsIgnoreCase(language.getKey())) {
                return language;
            }
        }
        return ko_kR;
    }

    public static LanguageType find(Integer value) {
        for (LanguageType language : LanguageType.values()) {
            if (value.equals(language.getValue())) {
                return language;
            }
        }
        return ko_kR;
    }

    public static boolean isKo (String lang){
        return ko_kR.key.equals(lang);
    }

    public static boolean isZh (String lang){
        return zh_CN.key.equals(lang);
    }

    public static boolean isEn (String lang){
        return en_US.key.equals(lang);
    }

}

