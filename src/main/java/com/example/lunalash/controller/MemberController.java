package com.example.lunalash.controller;

import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@Tag(name = "會員")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "查詢所有會員")
    @GetMapping("/getAllMembers")
    public List<MemberEntity> getAllMembers() {
        return memberService.getAllMembers();
    }

    @Operation(summary = "查詢單一會員資料")
    @GetMapping("/getMember/{id}")
    public MemberEntity getMemberById(@Parameter(description="會員編號")@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @Operation(summary = "新增會員資料")
    @PostMapping("/addMember")
    public MemberEntity createMember(@RequestBody MemberEntity member) {
        return memberService.createMember(member);
    }
}
