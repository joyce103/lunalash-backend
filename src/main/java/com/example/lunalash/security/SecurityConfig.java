package com.example.lunalash.security;

import com.example.lunalash.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

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
            
            // 錯誤處理：當沒有 Token 或 Token 驗證失敗時，回傳跟其他 API 一致的 JSON 格式，
            // 而不是 sendError 產生的空白/HTML 內容，這樣前端攔截器才能統一從 resultMsg 取得訊息
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    ApiResponse<Object> body = ApiResponse.fail(401, "請先登入 (查無有效 Token)", System.currentTimeMillis());
                    response.getWriter().write(objectMapper.writeValueAsString(body));
                })
            )
            
            .authorizeHttpRequests(auth -> auth
                // 允許所有 OPTIONS 請求 (這是為了解決 Vue Axios 跨域探路的預檢請求)
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() 
                
                .requestMatchers("/api/auth/login").permitAll() // 登入 API 完全放行
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/api/images/**").permitAll()

                // 預約系統：訪客不需要登入就能查詢操作項目/月曆/時段，以及送出預約申請
                // 注意 /api/admin/** 開頭的管理後台 API 不在這裡放行，一樣要走下面的 anyRequest().authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/calendar", "/api/available-slots", "/api/operation-catalog").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/appointments").permitAll()

                .anyRequest().authenticated() // 其他所有 API 都必須要有合法 Token
            );
        
        // 把我們的 JWT 海關，安插在 Spring Security 預設的帳號密碼驗證器之前
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    // 跨域 (CORS) 設定：只允許設定檔中列出的前端來源呼叫 API
    // 驗證資訊是透過 Authorization Header 帶 JWT，瀏覽器不需要也不會自動附帶 Cookie，
    // 所以不開放 allowCredentials，避免「允許所有來源 + 允許夾帶憑證」這種不安全的組合
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}