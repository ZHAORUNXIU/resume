package com.x.resume.common.constant;

/**
 * 用户Service
 *
 * @author runxiu.zhao
 * @date 2023-02-21 10:00:00
 */
public interface RedisConstant {

    /**
     * 用户登录短信验证码redisKey
     */
    String USER_LOGIN_CODE_KEY = "resume:user:login:code_";

    /**
     * 用户登录tokenRedisKey
     */
    String USER_LOGIN_TOKEN_KEY = "resume:user:login:token_";

}
