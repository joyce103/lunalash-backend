package com.example.lunalash.service;

import com.example.lunalash.dto.TransactionCreateRequest;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.repository.OperationItemRepository;
import com.example.lunalash.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRecordRepository transactionRepo;
    private final OperationItemRepository operationRepo;

    public TransactionService(
            TransactionRecordRepository transactionRepo,
            OperationItemRepository operationRepo
    ) {
        this.transactionRepo = transactionRepo;
        this.operationRepo = operationRepo;
    }

    @Transactional
    public Long createTransaction(TransactionCreateRequest request) {

        // 1️⃣ 建立交易
        TransactionRecordEntity transaction = new TransactionRecordEntity();
        transaction.setMemberId(request.memberId);
        transaction.setTransactionTime(request.transactionTime);
        transaction.setLashArtist(request.lashArtist);
        transaction.setAmountBeforeDiscount(request.amountBeforeDiscount);
        transaction.setAmountAfterDiscount(request.amountAfterDiscount);
        transaction.setDiscountType(request.discountType);
        transaction.setDiscountRate(request.discountRate);
        transaction.setPaymentMethod(request.paymentMethod);
        transaction.setRemark(request.remark);

        // 先存交易，讓 transactionId 自動生成
        transaction = transactionRepo.save(transaction);

        // 2️⃣ 建立操作項目
        List<OperationItemEntity> items = new ArrayList<>();
        for (var itemReq : request.operationItems) {
            OperationItemEntity item = new OperationItemEntity();
            item.setOperationName(itemReq.operationName);
            item.setTotalLashCount(itemReq.totalLashCount);
            item.setStyle(itemReq.style);
            item.setThickness(itemReq.thickness);
            item.setBrand(itemReq.brand);
            item.setCategory(itemReq.category);
            item.setGlueType(itemReq.glueType);
            item.setRemark(itemReq.remark);

            item.setTransaction(transaction);

            items.add(item);
        }

        // 一次存所有操作項目
        operationRepo.saveAll(items);

        return transaction.getTransactionId();
    }
}
