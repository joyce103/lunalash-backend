package com.example.lunalash.service;

import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.dto.TransactionDetailRequest;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.repository.TransactionDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionDetailService {

    private final TransactionDetailRepository repository;

    public TransactionDetailService(TransactionDetailRepository repository) {
        this.repository = repository;
    }

    /**
     * 刪除單筆交易明細
     */
    @Transactional
    public void deleteDetail(Long detailId) {
        if (!repository.existsById(detailId)) {
            throw new EntityNotFoundException("交易明細不存在");
        }
        repository.deleteById(detailId);
    }

    /**
     * 刪除某筆交易底下的所有明細
     */
    @Transactional
    public void deleteAllByTransaction(Long transactionId) {
        repository.deleteByTransaction_TransactionId(transactionId);
    }

    private TransactionDetailResponse toResponse(TransactionDetailEntity entity) {
        return new TransactionDetailResponse(
                entity.getTransactionDetailId(),
                entity.getItemName(),
                entity.getItemPrice(),
                entity.getQuantity()
        );
    }
}
