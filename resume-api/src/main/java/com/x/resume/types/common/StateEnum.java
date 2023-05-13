package com.x.resume.types.common;

public enum StateEnum {

    /**
     * 可用
     */
    ENABLE(1),

    /**
     * 不可用
     */
    DISABLE(0);

    private final Integer code;

    StateEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
