package com.x.resume.common.i18n.utils;

import com.x.resume.common.web.ReqContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
public final class I18nHelper {

    @Resource
    private LocaleResolver localeResolver;

    @Resource
    private MessageSource messageSource;

    public String getMessage(HttpServletRequest request, int code, Object... args) {
        return messageSource.getMessage(Integer.toString(code), args, null, localeResolver.resolveLocale(request));
    }

    public String getMessage(int code, Object... args) {
        return messageSource.getMessage(Integer.toString(code), args, null, resolveLocale());
    }

    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, null, resolveLocale());
    }

    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, null, locale);
    }

    private Locale resolveLocale() {
        // 没有上下文时取默认值
        ReqContext req = ReqContext.get();
        if (req == null || req.getRequest() == null) {
            return Locale.CHINESE;
        }

        return localeResolver.resolveLocale(req.getRequest());
    }
}