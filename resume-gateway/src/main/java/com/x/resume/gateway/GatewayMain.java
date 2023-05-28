package com.x.resume.gateway;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.x.resume.gateway.filter.ReqContextFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Locale;
import java.util.TimeZone;

@ComponentScan(value = "com.x.resume")
@EnableConfigurationProperties
@ImportResource({"classpath*:/META-INF/spring/context.xml"})
public class GatewayMain implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayMain.class);

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+9"));
        // json命名策略由驼峰改为下划线
        SerializeConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;

        ParserConfig.getGlobalInstance().setSafeMode(true);

        new SpringApplicationBuilder(GatewayMain.class).web(WebApplicationType.SERVLET).run(args);
    }

    @Bean
    public FilterRegistrationBean encodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("utf-8");
        filter.setForceEncoding(true);

        FilterRegistrationBean<CharacterEncodingFilter> bean = new FilterRegistrationBean<>();
        bean.addUrlPatterns("/*");
        bean.setFilter(filter);
        bean.setName("encodingFilter");
        bean.setOrder(5);
        return bean;
    }

    @Bean
    public FilterRegistrationBean reqContextFilter(ReqContextFilter filter) {
        FilterRegistrationBean<ReqContextFilter> bean = new FilterRegistrationBean<>();
        bean.addUrlPatterns("/*");
        bean.setFilter(filter);
        bean.setName("reqContextFilter");
        bean.setOrder(20);
        return bean;
    }

    @Bean
    @ConfigurationProperties(prefix = "server")
    public UndertowServletWebServerFactory servletContainerFactory() {
        return new UndertowServletWebServerFactory();
    }

    @Bean
    public ServletRegistrationBean appServlet(ApplicationContext context) {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setDetectAllHandlerMappings(false);
        servlet.setDetectAllViewResolvers(false);
        servlet.setPublishEvents(false);
        servlet.setApplicationContext(context);

        ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<>();
        bean.addUrlMappings("/");
        bean.setName("resume-gateway");
        bean.setOrder(1);
        bean.setServlet(servlet);
        bean.setLoadOnStartup(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean cors(UrlBasedCorsConfigurationSource corsConfigSource) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        bean.addUrlPatterns("/*");
        bean.setFilter(new CorsFilter(corsConfigSource));
        bean.setName("corsFilter");
        bean.setOrder(1);

        return bean;
    }

    @Override
    public void run(String... args) {
        LOG.info("服务器启动成功");
    }
}
