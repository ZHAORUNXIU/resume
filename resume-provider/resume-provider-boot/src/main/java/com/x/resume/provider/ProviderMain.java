package com.x.resume.provider;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.x.resume.common.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

@ComponentScan(basePackages = {"com.x.resume"})
@EnableTransactionManagement
@EnableConfigurationProperties
@ImportResource("classpath*:/META-INF/spring/context.xml")
public class ProviderMain {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderMain.class);


    public static void main(String[] args) throws Exception {

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+9"));

        ParserConfig.getGlobalInstance().setSafeMode(true);

        SerializeConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;

        ApplicationContext ctx = new SpringApplicationBuilder(ProviderMain.class).web(WebApplicationType.NONE).run(args);

        String file = ctx.getBean(AppCheck.class).createLockFile();

        LOG.info(Log.format("服务启动成功", Log.kv("lock", file)));

        ctx.getBean("SHUTDOWN_LATCH", CountDownLatch.class).await();
    }

    @Bean
    public AppCheck appCheck() {
        return new AppCheck();
    }

    @Bean("SHUTDOWN_LATCH")
    public CountDownLatch shutdownLatch() {
        return new CountDownLatch(1);
    }

    private static class AppCheck {
        @Value("${lockfile}")
        private String lockFile;

        String createLockFile() throws IOException {
            new File(lockFile).createNewFile();
            return lockFile;
        }
    }

}
