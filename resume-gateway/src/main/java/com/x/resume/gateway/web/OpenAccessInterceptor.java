package com.x.resume.gateway.web;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.x.resume.common.annotation.Acl;
import com.x.resume.common.constant.Code;
import com.x.resume.common.i18n.utils.I18nHelper;
import com.x.resume.common.model.Result;
import com.x.resume.common.web.ReqContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OpenAccessInterceptor implements HandlerInterceptor {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Resource
    private FastJsonHttpMessageConverter messageConverter;

    @Resource
    private I18nHelper i18nHelper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;

            // 方法有Acl时仅检查方法的，方法没有时检查类的
            Acl acl = hm.getMethod().getAnnotation(Acl.class);
            if (acl != null) {
                return verify(req, resp);
            }

            // 检查类的访问控制
            acl = hm.getBeanType().getAnnotation(Acl.class);
            if (acl != null) {
                return verify(req, resp);
            }
        }

        return true;
    }

    private boolean verify(HttpServletRequest req, HttpServletResponse resp) {
        if (ReqContext.get().getUserId() == null) {
            write(req, resp, Code.REJECT_ANONYMOUS);
            return false;
        }

        return true;
    }

    private void write(HttpServletRequest req, HttpServletResponse resp, int code) {
        Result<Void> result = Result.failure(code, i18nHelper.getMessage(req, code));
        try {
            messageConverter.write(result, MediaType.APPLICATION_JSON_UTF8, new ServletServerHttpResponse(resp));
        } catch (IOException e) {
            LOG.error("输出异常信息出错", e);
        }
    }

}
