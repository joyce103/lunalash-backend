package com.example.lunalash.controller;

import com.example.lunalash.dto.GoogleLoginRequest;
import com.example.lunalash.dto.LoginResponse;
import com.example.lunalash.security.GoogleTokenVerifierService;
import com.example.lunalash.security.GoogleTokenVerifierService.GoogleUserInfo;
import com.example.lunalash.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

// 訪客用 Google 登入，跟管理員登入 (AuthController) 是完全分開的兩套身分，
// 這裡發出的 token 角色固定是 "GUEST"，不會有機會被誤判成 ADMIN
// (SecurityConfig 的 fallback 規則是 anyRequest().hasRole("ADMIN"))
@RestController
@RequestMapping("/api/auth")
@Tag(name = "訪客登入")
public class GuestAuthController {

    private static final Logger log = LoggerFactory.getLogger(GuestAuthController.class);

    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final JwtUtils jwtUtils;

    public GuestAuthController(GoogleTokenVerifierService googleTokenVerifierService, JwtUtils jwtUtils) {
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.jwtUtils = jwtUtils;
    }

    @Operation(summary = "訪客用 Google 登入，換取站內 JWT (角色固定為 GUEST)")
    @PostMapping("/google")
    public LoginResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        GoogleUserInfo userInfo = googleTokenVerifierService.verify(request.getIdToken());

        String token = jwtUtils.generateToken(userInfo.email(), "GUEST");
        log.info("訪客 Google 登入成功：{}", userInfo.email());

        return new LoginResponse(token, userInfo.name());
    }
}
