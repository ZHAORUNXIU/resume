package com.x.resume.types.user;

/**
 * 用户状态
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
public enum UserStateEnum {

    /**
     * 正常
     */
    NORMAL(1),

    /**
     * 禁用
     */
    DISABLE(0),

    /**
     * 注销
     */
    CANCEL(2),

    ;


    private final Integer code;

    UserStateEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
