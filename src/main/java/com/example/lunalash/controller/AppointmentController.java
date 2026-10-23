package com.example.lunalash.controller;

import com.example.lunalash.dto.AppointmentCreateRequest;
import com.example.lunalash.dto.AppointmentLookupRequest;
import com.example.lunalash.dto.AppointmentPublicResponse;
import com.example.lunalash.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 訪客送出預約，完全公開，不需要登入、不需要建立會員帳號；
// 如果訪客有用 Google 登入 (role=GUEST 的 JWT)，建立預約時會自動帶入已驗證過的 email
@RestController
@RequestMapping("/api/appointments")
@Tag(name = "預約 (公開)")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "訪客送出預約申請 (回傳精簡資訊，含可用於之後查詢的查詢碼)")
    @PostMapping
    public AppointmentPublicResponse create(@Valid @RequestBody AppointmentCreateRequest request, Authentication authentication) {
        return appointmentService.createAppointment(request, extractGuestEmail(authentication));
    }

    @Operation(summary = "客人用電話+查詢碼查詢預約狀態 (用 POST 避免電話出現在網址/log 中)")
    @PostMapping("/lookup")
    public AppointmentPublicResponse lookup(@Valid @RequestBody AppointmentLookupRequest request) {
        return appointmentService.lookup(request);
    }

    @Operation(summary = "訪客用 Google 登入後，查詢自己 email 底下所有預約 (僅限 GUEST 角色)")
    @GetMapping("/my")
    public List<AppointmentPublicResponse> myAppointments(Authentication authentication) {
        return appointmentService.findByCustomerEmail(authentication.getName());
    }

    // 這支端點本身是 permitAll()，匿名呼叫時 authentication 會是 null；
    // 就算帶了 token，也只信任角色真的是 GUEST 的 token (例如管理員的 token 不該被當成訪客 email)
    private String extractGuestEmail(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        boolean isGuest = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_GUEST"::equals);
        return isGuest ? authentication.getName() : null;
    }
}
