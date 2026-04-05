package com.example.lunalash.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        registry.addInterceptor(executionTimeInterceptor)
        		.addPathPatterns("/**")
		        .excludePathPatterns(
		                "/api/auth/login",
		                "/swagger-ui/**",
		                "/swagger-ui.html",
		                "/v3/api-docs/**",
		                "/api-docs/**",
		                "/webjars/**"
		        );
    }
    
    // 跨域 (CORS) 設定
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 套用到所有的 API 路徑
                .allowedOriginPatterns("*") // 允許所有來源的前端 (開發用)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的 HTTP 請求方法
                .allowedHeaders("*") // 允許前端攜帶任何自訂的 Header
                .allowCredentials(true) // 允許前端攜帶 Cookie 或認證資訊 (如 Session)
                .maxAge(3600); // 預檢請求 (Preflight OPTIONS) 的快取時間，單位為秒
    }
}