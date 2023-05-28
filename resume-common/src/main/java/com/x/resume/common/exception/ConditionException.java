package com.x.resume.common.exception;

/**
 * 状态校验报错，（处理业务前，状态校验）
 */
public class ConditionException extends RuntimeException {

    private static final long serialVersionUID = 753884851145121863L;

    private int code;

    private String message;

    public ConditionException() {
        super();
    }

    public ConditionException(int code) {
        super("");
        this.code = code;
        this.message = "";
    }

    public ConditionException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ConditionException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public ConditionException(Throwable cause) {
        super(cause);
    }

    protected ConditionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
