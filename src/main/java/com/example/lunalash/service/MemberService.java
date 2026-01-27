package com.example.lunalash.service;

import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberEntity> getAllMembers() {
        return memberRepository.findAll();
    }

    public MemberEntity getMemberByPhone(String phone) {
        // 使用 Optional 的 orElseThrow，如果找不到就拋出例外
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("找不到手機號碼為 " + phone + " 的會員"));
    }

    public MemberEntity createMember(MemberEntity member) {
        return memberRepository.save(member);
    }
}
