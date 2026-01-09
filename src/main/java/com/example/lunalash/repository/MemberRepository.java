package com.example.lunalash.repository;

import com.example.lunalash.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // 依手機查會員（很常用）
    Optional<MemberEntity> findByPhone(String phone);

    // 判斷手機是否已存在（註冊 / 新增前檢查）
    boolean existsByPhone(String phone);
}
