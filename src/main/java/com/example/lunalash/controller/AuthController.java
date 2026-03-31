package com.example.lunalash.controller;

import com.example.lunalash.dto.LoginRequest;
import com.example.lunalash.dto.LoginResponse;
import com.example.lunalash.entity.AdminEntity;
import com.example.lunalash.exception.UnauthorizedException;
import com.example.lunalash.repository.AdminRepository;
import com.example.lunalash.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminRepository adminRepo;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AdminRepository adminRepo, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        
        // 防呆
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new UnauthorizedException("帳號或密碼不能為空"); 
        }

        // 尋找帳號 (使用 orElseThrow 讓程式碼更簡潔)
        AdminEntity admin = adminRepo.findByUsername(request.getUsername())
            .orElseThrow(() -> new UnauthorizedException("帳號或密碼錯誤"));

        // 比對密碼
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("帳號或密碼錯誤");
        }

        // 密碼正確，產生 Token
        String token = jwtUtils.generateToken(admin.getUsername());

        // 直接回傳資料本體
        // 你的 ResponseWrapperAdvice 會自動把它變成 { resultCode: 0, resultData: {token: "...", name: "..."}, ... }
        return new LoginResponse(token, admin.getName());
    }
}