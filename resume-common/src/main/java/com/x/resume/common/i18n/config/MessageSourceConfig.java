package com.x.resume.common.i18n.config;

import com.x.resume.common.i18n.context.LocalReloadableResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    @Bean(name = "messageSource")
    public MessageSource getMessageSource() {
        LocalReloadableResourceBundleMessageSource resourceBundleMessageSource = new LocalReloadableResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasenames("classpath*:/i18n.messages/resume");
        return resourceBundleMessageSource;
    }

    @Bean(name = "localeResolver")
    public LocaleResolver getLocaleResolver() {
        AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
        acceptHeaderLocaleResolver.setDefaultLocale(Locale.forLanguageTag("zh_CN"));
        return acceptHeaderLocaleResolver;
    }
}
