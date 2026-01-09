package com.example.lunalash;

import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testInsertMember() {
        MemberEntity member = new MemberEntity();
        member.setName("Luna");
        member.setGender("M");
        member.setBirthday(LocalDate.of(1998, 1, 1));
        member.setPhone("0912345678");
        member.setMemberLevel("一般");
        member.setLineId("line123");

        MemberEntity saved = memberRepository.save(member);

        System.out.println("會員新增成功，ID = " + saved.getMemberId());
    }
}
