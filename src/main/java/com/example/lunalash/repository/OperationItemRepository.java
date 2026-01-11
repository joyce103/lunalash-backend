package com.example.lunalash.repository;

import com.example.lunalash.entity.OperationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationItemRepository
        extends JpaRepository<OperationItemEntity, Long> {

    List<OperationItemEntity> findByTransaction_TransactionId(Long transactionId);

}
