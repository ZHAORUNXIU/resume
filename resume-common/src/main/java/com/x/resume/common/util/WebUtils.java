package com.x.resume.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class WebUtils {

    private static final String UNKNOWN_IP = "unknown";

    /**
     * 获取用户ip地址
     *
     * @param req http请求
     * @return ip地址
     */
    public static String getIp(HttpServletRequest req) {

        // 自定义
        String ip = Text.trimNull(req.getHeader("x-client-ip"));
        if (null != ip && !UNKNOWN_IP.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 获取第一个有效ip
        ip = Text.trimNull(req.getHeader("X-Forwarded-For"));
        if (null != ip && !UNKNOWN_IP.equalsIgnoreCase(ip)) {
            for (String item : Text.split(ip.replaceAll("[ \t]", ""), ',')) {
                if (UNKNOWN_IP.equalsIgnoreCase(item)) {
                    continue;
                }
                return item;
            }
        }

        ip = Text.trimNull(req.getHeader("X-Real-IP"));
        if (null != ip && !UNKNOWN_IP.equalsIgnoreCase(ip)) {
            return ip;
        }

        // 对端IP
        return req.getRemoteAddr();
    }

    public static Cookie getCookie(HttpServletRequest req, String name) {
        Cookie[] cs = req.getCookies();
        if (cs != null) {
            for (Cookie c : cs) {
                if (c.getName().equals(name)) {
                    return c;
                }
            }
        }
        return null;
    }

    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie c = new Cookie(name, value);
        c.setMaxAge(maxAge);
        return c;
    }

    public static void clearCookie(HttpServletResponse resp, String name, String domain, String path) {
        Cookie c = new Cookie(name, null);
        if (domain != null) {
            c.setDomain(domain);
        }
        c.setPath(path);
        c.setMaxAge(0);
        resp.addCookie(c);
    }
}

