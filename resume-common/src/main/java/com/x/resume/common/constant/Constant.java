package com.x.resume.common.constant;

import java.util.TimeZone;

/**
 * Constant
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
public interface Constant {

    String DOT = ".";

    String COMMA = ",";

    String asterisk = "*";

    String UNDERLINE = "_";

    String EQUAL_SIGN = "=";

    String SEMICOLON = ";";

    String PERCENT_SIGN = "%";

    String ID = "id";

    String USER_ID = "userId";

    /**
     * 默认北京时间东八区
     */
    TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+8:00");

    /**
     * 用户tokenKey
     */
    String LOGIN_TOKEN = "RESUME-TOKEN";

    /**
     * 0开头正则
     */
    String REGEX_ZERO_START = "^0+";

    /**
     * 默认的验证码
     */
    String DEFAULT_CODE = "123456";

    Long EXPIRED_MAX_TIME_STAMP = 2840112000000L;

    String REGEX_SPACE = "\\s";

}
