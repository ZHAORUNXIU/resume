package com.x.resume.gateway.filter;

import com.x.resume.common.client.RedisClient;
import com.x.resume.common.constant.Constant;
import com.x.resume.common.constant.RedisConstant;
import com.x.resume.common.util.Text;
import com.x.resume.common.util.WebUtils;
import com.x.resume.common.web.ReqContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReqContextFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ReqContextFilter.class);

    @Resource
    private LocaleResolver localeResolver;

    @Resource
    private RedisClient redisClient;


    /**
     * doFilterInternal
     *
     * @param req   req
     * @param resp  resp
     * @param chain chain
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse resp, @NotNull FilterChain chain) {
        long time = System.currentTimeMillis();

        try {
            ReqContext ctx = ReqContext.get();
            ctx.setRequest(req);
            ctx.setIp(WebUtils.getIp(req));
            ctx.setLocale(localeResolver.resolveLocale(req));
            ctx.setTimeZone(Constant.DEFAULT_TIME_ZONE);
            retrieveUser(ctx, req);
            ctx.setResponse(resp);

            chain.doFilter(req, resp);

        } catch (RuntimeException | IOException | ServletException e) {
            LOG.error("发生错误", e);
        } finally {
            ReqContext.remove();
        }

    }

    private void retrieveUser(ReqContext ctx, HttpServletRequest req) {
        String token = req.getHeader(Constant.LOGIN_TOKEN);
        String tokenKey = RedisConstant.USER_LOGIN_TOKEN_KEY + token;
        String userId = redisClient.get(tokenKey);
        if (Text.isNotBlank(userId)) {
            ctx.setUserId(Long.parseLong(userId));
        }
    }
}
