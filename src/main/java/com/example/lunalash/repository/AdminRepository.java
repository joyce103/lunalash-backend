package com.example.lunalash.repository;

import com.example.lunalash.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    
    // 登入時用來找這個帳號存不存在
    Optional<AdminEntity> findByUsername(String username);

    // 新增帳號時檢查帳號是否已被使用
    boolean existsByUsername(String username);

}