package com.x.resume.types.common.storage;

/**
 * @author runxiu.zhao
 * 状态，0-不可用，1-可用
 */

public enum StorageStateEnum {

    /**
     * 不可用
     */
    NONE(0),

    /**
     * 可用
     */
    ENABLE(1);

    private final Integer code;

    StorageStateEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
