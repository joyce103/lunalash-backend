package com.example.lunalash.controller;

import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 取得所有會員
    @GetMapping("/getAllMembers")
    public List<MemberEntity> getAllMembers() {
        return memberService.getAllMembers();
    }

    // 依照ID取得單一會員
    @GetMapping("/getMember/{id}")
    public MemberEntity getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    // 新增會員
    @PostMapping("/addMember")
    public MemberEntity createMember(@RequestBody MemberEntity member) {
        return memberService.createMember(member);
    }
}
