package com.example.lunalash.service;

import com.example.lunalash.dto.TransactionCreateRequest;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.repository.OperationItemRepository;
import com.example.lunalash.repository.TransactionDetailRepository;
import com.example.lunalash.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRecordRepository transactionRepo;
    private final OperationItemRepository operationRepo;
    private final EyelashAreaDetailRepository eyelashAreaDetailRepo;
    private final TransactionDetailRepository transactionDetailRepo;


    public TransactionService(
            TransactionRecordRepository transactionRepo,
            OperationItemRepository operationRepo,
            EyelashAreaDetailRepository eyelashAreaDetailRepo,
            TransactionDetailRepository transactionDetailRepo
    ) {
        this.transactionRepo = transactionRepo;
        this.operationRepo = operationRepo;
        this.eyelashAreaDetailRepo = eyelashAreaDetailRepo;
        this.transactionDetailRepo = transactionDetailRepo;
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
        for (var opReq : request.operationItems) {

            OperationItemEntity operationItem = new OperationItemEntity();
            operationItem.setOperationName(opReq.operationName);
            operationItem.setTotalLashCount(opReq.totalLashCount);
            operationItem.setStyle(opReq.style);
            operationItem.setThickness(opReq.thickness);
            operationItem.setBrand(opReq.brand);
            operationItem.setCategory(opReq.category);
            operationItem.setGlueType(opReq.glueType);
            operationItem.setRemark(opReq.remark);
            operationItem.setTransaction(transaction);

            operationItem = operationRepo.save(operationItem);

            List<EyelashAreaDetailEntity> areas = new ArrayList<>();
            for (var areaReq : opReq.eyelashAreaDetail) {
                EyelashAreaDetailEntity area = new EyelashAreaDetailEntity();
                area.setPosition(areaReq.position);
                area.setLashCount(areaReq.lashCount);
                area.setLashLengths(areaReq.lashLengths);
                area.setLashCurls(areaReq.lashCurls);
                area.setOperationItem(operationItem);

                areas.add(area);
            }

            eyelashAreaDetailRepo.saveAll(areas);
        }
        
        for (var detailReq : request.transactionDetails) {
            TransactionDetailEntity detail = new TransactionDetailEntity();
            detail.setItemName(detailReq.getItemName());
            detail.setItemPrice(detailReq.getItemPrice());
            detail.setQuantity(detailReq.getQuantity());
            detail.setTransaction(transaction);

            detail = transactionDetailRepo.save(detail);
        }

        return transaction.getTransactionId();
    }
}
