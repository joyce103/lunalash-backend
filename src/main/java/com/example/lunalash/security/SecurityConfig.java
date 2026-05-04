package com.example.lunalash.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // 注入我們剛剛寫好的海關攔截器
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 開啟跨域連線 (CORS)
            .csrf(csrf -> csrf.disable()) // 關閉 CSRF
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 不使用 Session
            
            // 錯誤處理：當沒有 Token 或 Token 驗證失敗時，明確回傳 401 Unauthorized
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "請先登入 (查無有效 Token)");
                })
            )
            
            .authorizeHttpRequests(auth -> auth
                // 允許所有 OPTIONS 請求 (這是為了解決 Vue Axios 跨域探路的預檢請求)
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() 
                
                .requestMatchers("/api/auth/login").permitAll() // 登入 API 完全放行
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/api/images/**").permitAll()
                .anyRequest().authenticated() // 其他所有 API 都必須要有合法 Token
            );
        
        // 把我們的 JWT 海關，安插在 Spring Security 預設的帳號密碼驗證器之前
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    // 跨域 (CORS) 設定：允許 Vue 前端 (例如 localhost:5173 或 localhost:3000) 呼叫 API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 允許所有來源 (開發期先開星號，上線可改為前端網址)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}