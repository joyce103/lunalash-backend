package com.example.lunalash.service;

import com.example.lunalash.dto.MemberUpdateRequest;
import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.MemberRepository;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
    	if (Boolean.TRUE.equals(member.getIsTermsAgreed())) {
    	    member.setTermsAgreedTime(LocalDateTime.now());
    	}
        return memberRepository.save(member);
    }
    
    public MemberEntity updateMember(MemberUpdateRequest request) {
        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("修改失敗，找不到此會員 ID: " + request.getMemberId()));

        if (StringUtils.hasText(request.getName())) member.setName(request.getName());
        if (StringUtils.hasText(request.getPhone())) member.setPhone(request.getPhone());
        if (StringUtils.hasText(request.getGender())) member.setGender(request.getGender());
        if (request.getBirthday() != null) member.setBirthday(request.getBirthday());
        if (StringUtils.hasText(request.getMemberLevel())) member.setMemberLevel(request.getMemberLevel());
        if (StringUtils.hasText(request.getLineId())) member.setLineId(request.getLineId());
        member.setIsTermsAgreed(request.getIsTermsAgreed());
        // 如果使用者勾選同意條款 則更新時間
    	if (Boolean.TRUE.equals(member.getIsTermsAgreed())) {
    	    member.setTermsAgreedTime(LocalDateTime.now());
    	}
        
        return memberRepository.save(member);
    }
}
