package com.x.resume.types.common.storage;

/**
 * @author runxiu.zhao
 * <p>
 * 加密状态，0-未加密，1-已加密
 */

public enum StorageCryptStateEnum {

    /**
     * 0-未加密
     */
    NONE(0),

    /**
     * 1-已加密
     */
    CRYPT(1);

    private final Integer code;

    StorageCryptStateEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
