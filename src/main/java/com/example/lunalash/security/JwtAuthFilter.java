package com.example.lunalash.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 1. 從 Request 中把 Token 抓出來
            String jwt = parseJwt(request);

            // 2. 如果有 Token，而且驗證是合法的
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                // 3. 從 Token 中把帳號 (username) 拿出來
                String username = jwtUtils.getUsernameFromToken(jwt);

                // 4. 製作一張「Spring Security 內部專用」的通行證，並告訴系統這個人已經登入了
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. 把通行證放進 SecurityContext 中 (放行！)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("無法設定使用者驗證: {}", e.getMessage());
        }

        // 繼續執行下一個過濾器或 API
        filterChain.doFilter(request, response);
    }

    // 小工具：從 Header 中擷取 Token (標準格式是 "Bearer xxxxxxx.yyyyyyy.zzzzzzz")
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // 把 "Bearer " 截掉，只保留後面的 Token
        }
        return null;
    }
    
    // 遇到 Swagger 相關路徑，直接閉眼放行，不執行 JwtAuthFilter
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/api-docs") ||
               path.startsWith("/webjars");
    }
}