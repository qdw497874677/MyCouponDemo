package com.imooc.coupon.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @PackageName:com.imooc.coupon.config
 * @ClassName: FeignConfiguration
 * @Description:
 * @date: 2020/11/5 0005 14:40
 */
@Configuration
public class FeignConfiguration {
    @Bean
    Logger.Level feignLoggerLevel() {
        //这里记录所有，根据实际情况选择合适的日志level
        return Logger.Level.FULL;
    }
}
