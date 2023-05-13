package com.x.resume.common.web;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ReqContext {

    private static ThreadLocal<ReqContext> local = ThreadLocal.withInitial(ReqContext::new);

    private HttpServletRequest request;

    private HttpServletResponse response;

    // 用户id
    private Long userId;

    // 用户ip
    private String ip;

    // 语言
    private Locale locale;

    // 时区
    private TimeZone timeZone;

    public static ReqContext get() {
        return local.get();
    }

    public static void remove() {
        local.remove();
    }

    public HttpServletRequest getRequest() {
        return get().request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }


}
