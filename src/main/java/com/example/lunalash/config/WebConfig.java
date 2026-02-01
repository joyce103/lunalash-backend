package com.example.lunalash.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ExecutionTimeInterceptor executionTimeInterceptor;

    public WebConfig(ExecutionTimeInterceptor executionTimeInterceptor) {
        this.executionTimeInterceptor = executionTimeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 套用到所有的 API 路徑
        registry.addInterceptor(executionTimeInterceptor).addPathPatterns("/**");
    }
}