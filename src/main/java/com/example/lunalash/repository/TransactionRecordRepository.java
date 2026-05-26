package com.example.lunalash.repository;

import com.example.lunalash.entity.TransactionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRecordRepository
        extends JpaRepository<TransactionRecordEntity, Long> {

    List<TransactionRecordEntity> findByMemberId(Long memberId);
    List<TransactionRecordEntity> findByTransactionId(Long transactionId);
}
