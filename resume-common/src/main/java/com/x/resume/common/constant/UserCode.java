package com.x.resume.common.constant;

/**
 * 用户模块Code
 *
 * @author runxiu.zhao
 * @date 2023-02-21 10:00:00
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
     * 用户状态异常
     */
    int USER_STATUS_LOCAL = 2003;

    /**
     * 用户点赞不存在
     */
    int USER_PRAISE_NOT_FOUND = 2004;

    /**
     * 用户点赞已存在
     */
    int USER_PRAISE_ALREADY_EXISTS = 2005;

    /**
     * 用户点赞已取消
     */
    int USER_PRAISE_ALREADY_CANCEL = 2006;

    /**
     * 用户关注不存在
     */
    int USER_FOCUS_NOT_FOUND = 2007;

    /**
     * 用户关注已存在
     */
    int USER_FOCUS_ALREADY_EXISTS = 2008;

    /**
     * 用户关注已取消
     */
    int USER_FOCUS_ALREADY_CANCEL = 2009;

    /**
     * 用户收藏不存在
     */
    int USER_COLLECT_NOT_FOUND = 2007;

    /**
     * 用户收藏已存在
     */
    int USER_COLLECT_ALREADY_EXISTS = 2008;

    /**
     * 用户收藏已取消
     */
    int USER_COLLECT_ALREADY_CANCEL = 2009;

    /**
     * 此用户uid已是评测人
     */
    int USER_ALREADY_AS_EVALUATOR = 2010;

    /**
     * 评测人用户类型 3
     */
    Integer EVALUATOR_USER_TYPE = 3;

}
