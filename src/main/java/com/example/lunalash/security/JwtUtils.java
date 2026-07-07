package com.example.lunalash.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // 將字串密鑰轉換為 HMAC SHA 加密所需的 Key 物件
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. 產生 Token (發證)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 把帳號塞進 Token 裡面
                .setIssuedAt(new Date()) // 發證時間
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // 到期時間
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 蓋上防偽印章
                .compact();
    }

    // 2. 從 Token 中取出帳號 (解碼)
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }

    // 3. 驗證 Token 是否合法
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // 如果過期、被竄改、格式不對，都會跑到這裡
            log.warn("JWT 驗證失敗: {}", e.getMessage());
        }
        return false;
    }
}