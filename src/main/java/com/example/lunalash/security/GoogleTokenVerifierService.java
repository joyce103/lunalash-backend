package com.example.lunalash.security;

import com.example.lunalash.exception.UnauthorizedException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

// 驗證前端 Google Sign-In 拿到的 ID Token：簽名、issuer、audience、過期時間
// 都由 GoogleIdTokenVerifier 處理 (會自動抓 Google 的公開金鑰並快取)，
// 這裡只負責額外檢查 email 是否已驗證，並把驗證結果包成站內好用的物件
@Component
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(@Value("${google.oauth.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    // 驗證成功回傳 email/name；驗證失敗或 email 未經 Google 驗證都丟 UnauthorizedException
    public GoogleUserInfo verify(String idTokenString) {
        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            throw new UnauthorizedException("Google 登入驗證失敗");
        }

        if (idToken == null) {
            throw new UnauthorizedException("Google 登入驗證失敗");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new UnauthorizedException("此 Google 帳號的 email 尚未經過驗證，無法登入");
        }

        String name = (String) payload.get("name");
        return new GoogleUserInfo(payload.getEmail(), name);
    }

    public record GoogleUserInfo(String email, String name) {
    }
}
