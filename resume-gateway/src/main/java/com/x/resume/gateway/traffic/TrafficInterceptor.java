package com.x.resume.gateway.traffic;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.x.resume.common.i18n.utils.I18nHelper;
import com.x.resume.common.web.ReqContext;
import com.x.resume.common.model.Result;
import com.x.resume.common.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.x.resume.common.util.Log.format;
import static com.x.resume.common.util.Log.kv;

public class TrafficInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TrafficInterceptor.class);

    @Resource
    private FastJsonHttpMessageConverter messageConverter;

    @Resource
    private I18nHelper i18nHelper;

    @Resource
    private TrafficController trafficController;

    @Value("${deploy.trafficControl.enabled}")
    private boolean enabled;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        if (enabled && (handler instanceof HandlerMethod)) {
            HandlerMethod hm = (HandlerMethod) handler;
            LocalDateTime ldt = LocalDateTime.now();

            // 方法的
            TrafficControl[] tcs = hm.getMethod().getAnnotationsByType(TrafficControl.class);
            if (tcs != null) {
                return control(req, resp, ldt, tcs);
            }

            // 类的
            tcs = hm.getBeanType().getAnnotationsByType(TrafficControl.class);
            return control(req, resp, ldt, tcs);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object handler, ModelAndView mv) {
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception e) {
    }

    private boolean control(HttpServletRequest req, HttpServletResponse resp, LocalDateTime ldt, TrafficControl[] tcs) {
        String uri = req.getRequestURI();
        for (TrafficControl tc : tcs) {
            String value;
            if (tc.type() == TrafficControlType.IP) {
                value = ReqContext.get().getIp();
                if (Text.isBlank(value)) {
                    LOG.warn("未获得用户IP, 使用默认IP");
                    value = "global";
                }
            } else if (tc.type() == TrafficControlType.USER) {
                Long userId = ReqContext.get().getUserId();
                if (userId == null) {
                    LOG.error(format("用户类型的接口流量控制不允许在非受限接口使用", kv("uri", uri)));
                    continue;
                }

                value = Long.toString(userId);
            } else {
                LOG.error(format("接口流量控制不支持该目标类型", kv("type", tc.type())));
                continue;
            }

            if (!trafficController.control(ldt, tc, value + "@" + uri)) {
                LOG.error(format("触发流控", kv("type", tc.type()), kv("value", value), kv("uri", uri)));
                write(req, resp, Code.FREQUENT_INVOKE);
                return false;
            }
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
