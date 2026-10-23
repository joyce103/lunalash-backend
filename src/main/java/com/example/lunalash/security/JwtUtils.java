package com.example.lunalash.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // 將字串密鑰轉換為 HMAC SHA 加密所需的 Key 物件
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. 產生 Token (發證)，role 用來區分這張 token 是哪種身分 (目前只有 "ADMIN")
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username) // 把帳號塞進 Token 裡面
                .claim("role", role) // 身分角色，供 SecurityConfig 做授權判斷
                .issuedAt(new Date()) // 發證時間
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // 到期時間
                .signWith(getSigningKey(), Jwts.SIG.HS256) // 蓋上防偽印章
                .compact();
    }

    // 2. 從 Token 中取出帳號 (解碼)
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // 2-1. 從 Token 中取出角色；舊格式 token 沒有這個 claim 時回傳 null
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    // 3. 驗證 Token 是否合法
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            // 如果過期、被竄改、格式不對，都會跑到這裡
            log.warn("JWT 驗證失敗: {}", e.getMessage());
        }
        return false;
    }
}