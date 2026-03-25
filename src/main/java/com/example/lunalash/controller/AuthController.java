package com.example.lunalash.controller;

import com.example.lunalash.dto.LoginRequest;
import com.example.lunalash.dto.LoginResponse;
import com.example.lunalash.entity.AdminEntity;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        // 1. 去資料庫尋找這個帳號
        Optional<AdminEntity> adminOpt = adminRepo.findByUsername(request.getUsername());
        
        // 如果找不到這個人，回傳 401 未授權
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
        }
        
        AdminEntity admin = adminOpt.get();

        // 2. 比對密碼 (將前端傳來的明文密碼，跟資料庫裡的 BCrypt 亂碼進行比對)
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
        }

        // 3. 密碼正確！請 JwtUtils 製作一張專屬 Token
        String token = jwtUtils.generateToken(admin.getUsername());

        // 4. 把 Token 跟管理員的名字回傳給前端
        return ResponseEntity.ok(new LoginResponse(token, admin.getName()));
    }
}