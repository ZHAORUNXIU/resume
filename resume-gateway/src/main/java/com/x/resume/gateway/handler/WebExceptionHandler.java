package com.x.resume.gateway.handler;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.x.resume.common.constant.Code;
import com.x.resume.common.exception.BusinessException;
import com.x.resume.common.exception.ConditionException;
import com.x.resume.common.i18n.utils.I18nHelper;
import com.x.resume.common.model.Result;
import com.x.resume.common.util.Text;
import com.x.resume.common.web.ReqContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import static com.x.resume.common.util.Log.format;
import static com.x.resume.common.util.Log.kv;

@RestController
@ControllerAdvice
public class WebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);

    private static final String LOG_PREFIX = "拦截器服务:";

    private static final String URI = "uri";

    private static final String CODE = "code";

    private static final String MESSAGE = "message";

    private static final String USER_ID = "userId";

    private static final String HEADERS = "Headers";

    @Resource
    private I18nHelper i18nHelper;

    @Resource
    private FastJsonHttpMessageConverter messageConverter;

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleHttpMessageNotReadableException(HttpServletRequest req, HttpServletResponse res, HttpMessageNotReadableException e) {
        LOG.error(format(LOG_PREFIX + "参数解析失败",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, e.getMessage()),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        this.write(res, Code.ILLEGAL_REQUEST, i18nHelper.getMessage(req, Code.ILLEGAL_REQUEST));
    }

    /**
     * 405 - Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void handleHttpRequestMethodNotSupportedException(HttpServletRequest req, HttpServletResponse res, HttpRequestMethodNotSupportedException e) {
        LOG.error(format(LOG_PREFIX + "不支持当前请求方法",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, e.getMessage()),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        this.write(res, Code.ILLEGAL_REQUEST, i18nHelper.getMessage(req, Code.ILLEGAL_REQUEST));
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public void handleHttpMediaTypeNotSupportedException(HttpServletRequest req, HttpServletResponse res, Exception e) {
        LOG.error(format(LOG_PREFIX + "不支持当前媒体类型",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, e.getMessage()),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        this.write(res, Code.ILLEGAL_REQUEST, i18nHelper.getMessage(req, Code.ILLEGAL_REQUEST));
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void missingServletRequestPartException(HttpServletRequest req, HttpServletResponse res, MissingServletRequestParameterException e) {
        LOG.error(format(LOG_PREFIX + "Miss参数异常",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, e.getMessage()),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        this.write(res, Code.ILLEGAL_PARAM, i18nHelper.getMessage(req, Code.ILLEGAL_PARAM));
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void methodArgumentNotValidException(HttpServletRequest req, HttpServletResponse res, MethodArgumentNotValidException e) {
        LOG.warn(format(LOG_PREFIX + "方法参数异常",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, e.getMessage()),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        this.write(res, Code.ILLEGAL_PARAM, i18nHelper.getMessage(req, Code.ILLEGAL_PARAM));
    }

    /**
     * 业务报错
     */
    @ExceptionHandler(BusinessException.class)
    public void businessException(HttpServletRequest req, HttpServletResponse res, BusinessException e) {
        String message = e.getMessage();
        Integer code = e.getCode();

        LOG.warn(format(LOG_PREFIX + "业务异常",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, message),
                kv(CODE, code),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        if (Text.isEmpty(message)) {
            message = i18nHelper.getMessage(req, code);
        }
        if (Text.isEmpty(message)) {
            message = String.valueOf(code);
        }

        this.write(res, e.getCode(), message);
    }

    /**
     * 状态报错
     */
    @ExceptionHandler(ConditionException.class)
    public void conditionException(HttpServletRequest req, HttpServletResponse res, ConditionException e) {
        String message = e.getMessage();

        LOG.warn(format(LOG_PREFIX + "状态报错",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, message),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())));

        if (Text.isEmpty(message)) {
            message = i18nHelper.getMessage(req, e.getCode());
        }
        if (Text.isEmpty(message)) {
            message = String.valueOf(e.getCode());
        }

        this.write(res, e.getCode(), message);
    }

    /**
     * 500 - 未知错误
     */
    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletRequest req, HttpServletResponse res, Exception e) {
        LOG.error(format(LOG_PREFIX + "未可知错误发生",
                kv(URI, req.getRequestURI()),
                kv(MESSAGE, e.getMessage()),
                kv(HEADERS, getHeaders(req)),
                kv(USER_ID, ReqContext.get().getUserId())), e);

        String message = i18nHelper.getMessage(req, Code.SYSTEM_ERROR);

        this.write(res, Code.SYSTEM_ERROR, message);
    }

    private void write(HttpServletResponse res, int code, String message) {
        try {
            messageConverter.write(Result.failure(code, message), MediaType.APPLICATION_JSON_UTF8, new ServletServerHttpResponse(res));
        } catch (IOException e) {
            LOG.error(format("输出异常信息出错"), e);
        }
    }

    /**
     * 获取Header
     *
     * @param req 请求对象
     * @return String
     */
    private String getHeaders(HttpServletRequest req) {
        StringBuilder builder = new StringBuilder();
        String key;
        Enumeration<String> e = req.getHeaderNames();
        while (e.hasMoreElements()) {
            key = e.nextElement();
            builder.append(key).append("=").append(req.getHeader(key)).append(";");
        }
        return builder.toString();
    }

}
