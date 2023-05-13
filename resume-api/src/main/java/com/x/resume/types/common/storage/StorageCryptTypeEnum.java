package com.x.resume.types.common.storage;

/**
 * @author runxiu.zhao
 * 加密类型，0-不需要加密，1-需要AES256加密，2-需要DES加密
 */

public enum StorageCryptTypeEnum {

    /**
     * 不需要加密
     */
    NONE(0),

    /**
     * 需要AES256加密
     */
    AES256(1),

    /**
     * 需要DES加密
     */
    DES(2);

    private final Integer code;

    StorageCryptTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
