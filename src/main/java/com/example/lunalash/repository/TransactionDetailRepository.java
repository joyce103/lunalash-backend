package com.example.lunalash.repository;

import com.example.lunalash.entity.TransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDetailRepository
        extends JpaRepository<TransactionDetailEntity, Long> {

    // 依交易單號查詢所有明細
    List<TransactionDetailEntity> findByTransaction_TransactionId(Long transactionId);

    // 依交易單號刪除所有明細
    void deleteByTransaction_TransactionId(Long transactionId);
}
