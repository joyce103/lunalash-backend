package com.example.lunalash.service;

import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberEntity> getAllMembers() {
        return memberRepository.findAll();
    }

    public MemberEntity getMemberById(Long id) {
        Optional<MemberEntity> member = memberRepository.findById(id);
        return member.orElse(null); // 或者丟出自定義例外
    }

    public MemberEntity createMember(MemberEntity member) {
        return memberRepository.save(member);
    }
}
