package com.example.lunalash.service;

import com.example.lunalash.dto.TransactionDetailRequest;
import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.repository.TransactionDetailRepository;
import com.example.lunalash.repository.TransactionRecordRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionDetailService {

    private final TransactionDetailRepository repository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionDetailService(TransactionDetailRepository repository, TransactionRecordRepository transactionRecordRepository) {
        this.repository = repository;
        this.transactionRecordRepository = transactionRecordRepository;
    }
    
    /**
     * 取得交易下所有明細  
     */
    public List<TransactionDetailEntity> getTransactionDetail(Long transactionId) {
        return repository.findByTransaction_TransactionId(transactionId);
    }

    /**
     * 新增交易明細
     */
    @Transactional
    public TransactionDetailEntity createTransactionDetail(
            TransactionDetailRequest request
    ) {
    	TransactionRecordEntity transaction = transactionRecordRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        TransactionDetailEntity detail = new TransactionDetailEntity();
        detail.setTransaction(transaction);
        detail.setItemName(request.getItemName());
        detail.setItemPrice(request.getItemPrice());
        detail.setQuantity(request.getQuantity());

        return repository.save(detail);
    }
    public TransactionDetailEntity createTransactionDetail(TransactionDetailEntity detail) {
        return repository.save(detail);
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
