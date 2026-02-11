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

import java.math.BigDecimal;
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
    	BigDecimal originalPrice = request.getItemPrice();
        BigDecimal finalPrice = originalPrice; // 預設等於原價

        if ("R".equalsIgnoreCase(request.getDiscountType())) {
            // 打折邏輯：原價 * 折扣率 (例如 1000 * 0.9 = 900)
            finalPrice = originalPrice.multiply(request.getDiscountRate());
            detail.setDiscountType("R");
        } else if ("A".equalsIgnoreCase(request.getDiscountType())) {
            // 折價邏輯：原價 - 折扣金額 (例如 1000 - 100 = 900)
            finalPrice = originalPrice.subtract(request.getDiscountPrice());
            detail.setDiscountType("A");
        } else {
        	// 前端傳非定義值時，統一回Ｎ
        	detail.setDiscountType("N");
        }
        // 	將計算後價格寫回折扣後價格
        detail.setActualPrice(finalPrice);
        detail.setTransaction(transaction);
        detail.setItemName(request.getItemName());
        detail.setItemPrice(request.getItemPrice());
        detail.setQuantity(request.getQuantity());
        detail.setDiscountRate(request.getDiscountRate());
        detail.setDiscountPrice(request.getDiscountPrice());
        
        return toResponse(repository.save(detail));
    }

    // 刪除單筆交易明細
    @Transactional
    public void deleteDetail(Long detailId) {
        if (!repository.existsById(detailId)) {
            throw new ResourceNotFoundException("交易明細不存在。");
        }
        repository.deleteById(detailId);
    }

    // 刪除某筆交易底下的所有明細
    @Transactional
    public void deleteAllByTransaction(Long transactionId) {
        // 先檢查是否存在，以符合你想要「失敗時回傳 404」的邏輯
        List<TransactionDetailEntity> details = repository.findByTransaction_TransactionId(transactionId);
        if (details.isEmpty()) {
            throw new ResourceNotFoundException("找不到該交易下的任何明細。");
        }
        repository.deleteByTransaction_TransactionId(transactionId);
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