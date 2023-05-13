package com.x.resume.gateway.web;

import com.x.resume.common.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.x.resume.common.util.Log.format;

/**
 * 验证域名
 */
public class CorsDomainConfiguration extends CorsConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(CorsDomainConfiguration.class);

    @Override
    public String checkOrigin(String requestOrigin) {

        if (!StringUtils.hasText(requestOrigin)) {
            LOG.warn(Log.format("请求源域名不存在", Log.kv("requestOrigin", requestOrigin)));
            return null;
        }

        if ("null".equals(requestOrigin)) {
            LOG.warn(Log.format("请求源域名为null", Log.kv("requestOrigin", requestOrigin)));
            return null;
        }

        List<String> allowedOrigins = getAllowedOrigins();

        if (ObjectUtils.isEmpty(allowedOrigins)) {
            return null;
        }

        if (allowedOrigins.contains(ALL)) {
            if (!Boolean.TRUE.equals(getAllowCredentials())) {
                return ALL;
            }
            return requestOrigin;
        }

        String domain;
        try {
            URL url = new URL(requestOrigin);
            domain = url.getHost().toLowerCase();
        } catch (MalformedURLException e) {
            LOG.error(Log.format("跨域请求url解析失败", Log.kv("requestOrigin", requestOrigin)), e);
            return null;
        }

        for (String allowedOrigin : allowedOrigins) {
            if (allowedOrigin.startsWith("*.")) {
                if (domain.endsWith(allowedOrigin.substring(1))) {
                    return requestOrigin;
                }
            } else if (domain.equals(allowedOrigin)) {
                return requestOrigin;
            }
        }

        return null;
    }

}
