package com.x.resume.common.exception;

/**
 * 业务报错（需要处理事务，和业务逻辑）
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 753884851145121863L;

    private int code;

    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(int code) {
        super("");
        this.code = code;
        this.message = "";
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    protected BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
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

