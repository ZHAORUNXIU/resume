package com.x.resume.provider.filter;

import com.alibaba.dubbo.common.Constants;
import com.x.resume.common.exception.BusinessException;
import com.x.resume.common.i18n.utils.I18nHelper;
import com.x.resume.common.util.SpringUtils;
import com.x.resume.common.util.Text;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Locale;

/**
 * 生产者拦截器，提供给provider服务使用
 */
@Activate(group = {Constants.CONSUMER}, order = -30000)
public class ProviderFilter implements Filter {

    @SuppressWarnings({"rawtypes"})
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        String locale = RpcContext.getContext().getAttachment("locale");
        if (Text.isBlank(locale)) {
            return result;
        }

        // 判断业务异常是否有文案，如果没有则获取文案返回
        Throwable throwable = result.getException();
        if (throwable instanceof BusinessException) {
            BusinessException exception = (BusinessException) throwable;
            if (Text.isNotBlank(exception.getMessage())) {
                return result;
            }
            result.setException(new BusinessException(exception.getCode(), getMessage(exception.getCode(), locale)));
            return result;
        }

        // 判断错误码是否有文案，如果没有则获取文案返回
        if (result.getValue() instanceof com.x.resume.common.model.Result) {
            com.x.resume.common.model.Result res = (com.x.resume.common.model.Result) result.getValue();
            if (res.isSuccess()) {
                return result;
            }
            if (Text.isNotBlank(res.getMessage())) {
                return result;
            }
            res.setMessage(this.getMessage(res.getCode(), locale));
            result.setValue(res);
        }
        return result;
    }

    protected String getMessage(int code, String locale) {
        I18nHelper i18nHelper = SpringUtils.getBean(I18nHelper.class);
        return i18nHelper.getMessage(String.valueOf(code), Locale.forLanguageTag(locale));
    }
}