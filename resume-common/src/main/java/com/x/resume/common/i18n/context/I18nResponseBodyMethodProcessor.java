package com.x.resume.common.i18n.context;

import com.x.resume.common.i18n.utils.I18nHelper;
import com.x.resume.common.model.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

public class I18nResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

    @Resource
    private I18nHelper i18nHelper;

    public I18nResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    protected <T> void writeWithMessageConverters(T value, MethodParameter returnType,
                                                  ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
            throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        if (value instanceof Result) {
            Result r = (Result) value;
            if (!r.isSuccess() && null == r.getMessage()) {
                String message = i18nHelper.getMessage(r.getCode());
                if (null != message) {
                    r.setMessage(i18nHelper.getMessage(r.getCode()));
                }
            }
        }

        super.writeWithMessageConverters(value, returnType, inputMessage, outputMessage);
    }
}
