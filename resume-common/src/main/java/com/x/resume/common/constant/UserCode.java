package com.x.resume.common.constant;

/**
 * 用户模块Code
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
public interface UserCode {

    /**
     * 用户不存在
     */
    int USER_NOT_FOUND = 2000;

    /**
     * 用户登录失败
     */
    int LOGIN_FAILED = 2001;

    /**
     * 用户登录验证码不正确
     */
    int LOGIN_CODE_INCORRECT = 2002;

    /**
     * 用户状态不可用
     */
    int USER_STATUS_DISABLE = 2003;

    /**
     * 用户登录验证码尚未过期
     */
    int LOGIN_CODE_NOT_EXPIRED = 2004;

}
