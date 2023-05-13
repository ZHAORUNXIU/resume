package com.x.resume.common.constant;

public interface Code {

    /**
     * 成功
     */
    int SUCCESS = 200;

    /**
     * 拒绝访问
     */
    int FORBIDDEN = 403;

    /**
     * 访问的资源不存在
     */
    int NOT_FOUND = 404;

    /**
     * 系统错误
     */
    int SYSTEM_ERROR = 500;

    /**
     * 无效请求
     */
    int ILLEGAL_REQUEST = 501;

    /**
     * 无效参数
     */
    int ILLEGAL_PARAM = 502;

    /**
     * 请求参数过大
     */
    int EXCEED_SIZE = 503;

    /**
     * 缺少参数
     */
    int PARAM_MISSING = 504;

    /**
     * 频繁调用
     */
    int FREQUENT_INVOKE = 510;

    /**
     * 黑名单用户
     */
    int BLACKLIST = 511;

    /**
     * 拒绝匿名访问
     */
    int REJECT_ANONYMOUS = 512;

}
