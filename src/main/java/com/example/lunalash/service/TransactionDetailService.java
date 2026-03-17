package com.example.lunalash.service;

import com.example.lunalash.dto.TransactionDetailRequest;
import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.TransactionDetailRepository;
import com.example.lunalash.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionDetailService {

    private final TransactionDetailRepository repository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionDetailService(TransactionDetailRepository repository, TransactionRecordRepository transactionRecordRepository) {
        this.repository = repository;
        this.transactionRecordRepository = transactionRecordRepository;
    }
    
    // 取得交易下所有明細並轉為 Response DTO
    public List<TransactionDetailResponse> getTransactionDetailsByTransactionId(Long transactionId) {
        List<TransactionDetailEntity> details = repository.findByTransaction_TransactionId(transactionId);
        if (details.isEmpty()) {
            throw new ResourceNotFoundException("找不到交易單號為 " + transactionId + " 的明細");
        }
        return details.stream()
                      .map(this::toResponse)
                      .collect(Collectors.toList());
    }

    // 新增交易明細並回傳 Response DTO
    @Transactional
    public TransactionDetailResponse createTransactionDetail(TransactionDetailRequest request) {
    	TransactionRecordEntity transaction = transactionRecordRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到對應的交易。"));

        TransactionDetailEntity detail = new TransactionDetailEntity();

        detail.setTransaction(transaction);
        detail.setItemName(request.getItemName());
        detail.setItemPrice(request.getItemPrice());
        detail.setQuantity(request.getQuantity());
        detail.setDiscountType(request.getDiscountType());
        detail.setDiscountRate(request.getDiscountRate());
        detail.setDiscountPrice(request.getDiscountPrice());
        // 計算折扣後價格
        detail.calculatePrices();
        if (transaction.getTransactionDetails() != null) {
        	transaction.getTransactionDetails().add(detail);
        }
        transaction.syncAmounts(); 
        transactionRecordRepository.save(transaction);
        return toResponse(repository.save(detail));
    }

    // 刪除單筆交易明細
    @Transactional
    public void deleteDetail(Long detailId) {
    	// 查詢交易明細
    	TransactionDetailEntity detail = repository.findById(detailId)
                .orElseThrow(() -> new ResourceNotFoundException("交易明細不存在。"));
    	// 查詢交易
    	TransactionRecordEntity transaction = detail.getTransaction();
    	// 從交易中移除明細
    	if (transaction.getTransactionDetails() != null) {
    		transaction.getTransactionDetails().remove(detail);
    	}
        transaction.syncAmounts(); 
        transactionRecordRepository.save(transaction);
        repository.deleteById(detailId);
    }

    // 刪除某筆交易底下的所有明細
    @Transactional
    public void deleteAllByTransaction(Long transactionId) {
    	TransactionRecordEntity transaction = transactionRecordRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到對應的交易。"));

        // 撈出所有明細
        List<TransactionDetailEntity> details = repository.findByTransaction_TransactionId(transactionId);
        if (details.isEmpty()) {
            throw new ResourceNotFoundException("找不到該交易下的任何明細。");
        }
        // 清空主檔中的明細 List
        if (transaction.getTransactionDetails() != null) {
            transaction.getTransactionDetails().clear();
        }
        // 重新計算總金額 因為 List 已經清空，這裡算完後總金額會變 0
        transaction.syncAmounts();
        transactionRecordRepository.save(transaction);
        repository.deleteAll(details);
    }
    
    // 更新單筆交易明細
    @Transactional
    public TransactionDetailResponse updateTransactionDetail(TransactionDetailRequest request) {
    	if (request.getTransactionDetailId() == null) {
    		throw new IllegalArgumentException("更新明細時，必須提供 detailId。");
    	}
        TransactionDetailEntity detail = repository.findById(request.getTransactionDetailId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到要更新的交易明細。"));

        TransactionRecordEntity transaction = detail.getTransaction();

        if (!transaction.getTransactionId().equals(request.getTransactionId())) {
            throw new IllegalArgumentException("明細所屬的交易單號不可更改。");
        }

        detail.setItemName(request.getItemName());
        detail.setItemPrice(request.getItemPrice());
        detail.setQuantity(request.getQuantity());
        
        // 折扣欄位如果前端傳 null，就代表清除該明細的折扣
        detail.setDiscountType(request.getDiscountType());
        detail.setDiscountRate(request.getDiscountRate());
        detail.setDiscountPrice(request.getDiscountPrice());

        // 重新計算明細價格與總價
        detail.calculatePrices();
        transaction.syncAmounts();

        transactionRecordRepository.save(transaction);
        return toResponse(repository.save(detail));
    }

    // 統一轉換邏輯
    private TransactionDetailResponse toResponse(TransactionDetailEntity entity) {
        return new TransactionDetailResponse(
                entity.getTransactionDetailId(),
                entity.getItemName(),
                entity.getItemPrice(),
                entity.getQuantity(),
                entity.getDiscountType(),
                entity.getDiscountRate(),
                entity.getDiscountPrice(),
                entity.getActualPrice()
        );
    }
}