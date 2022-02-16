package com.info.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "filter", name = "loginFilter", havingValue = "no")
public class test {
    @Bean
    public void testInit() {
        System.out.println("测试");
    }
}
