package com.example.lunalash.service;

import com.example.lunalash.dto.OperationItemResponse;
import com.example.lunalash.dto.EyelashAreaDetailResponse;
import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.dto.TransactionCreateRequest;
import com.example.lunalash.dto.TransactionResponse;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
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
            operationItem.setOperationName(opReq.getOperationName());
            operationItem.setTotalLashCount(opReq.getTotalLashCount());
            operationItem.setStyle(opReq.getStyle());
            operationItem.setThickness(opReq.getThickness());
            operationItem.setBrand(opReq.getBrand());
            operationItem.setCategory(opReq.getCategory());
            operationItem.setGlueType(opReq.getGlueType());
            operationItem.setRemark(opReq.getRemark());
            operationItem.setTransaction(transaction);

            operationItem = operationRepo.save(operationItem);

            List<EyelashAreaDetailEntity> areas = new ArrayList<>();
            for (var areaReq : opReq.getEyelashAreaDetail()) {
                EyelashAreaDetailEntity area = new EyelashAreaDetailEntity();
                area.setPosition(areaReq.getPosition());
                area.setLashCount(areaReq.getLashCount());
                area.setLashLengths(areaReq.getLashLengths());
                area.setLashCurls(areaReq.getLashCurls());
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
    
    public List<TransactionRecordEntity> getTransactionsByMemberId(Long memberId) {
    	List<TransactionRecordEntity> transactions = transactionRepo.findByMemberId(memberId);
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("查無此會員");
        }
        return transactions;
    }
    
    @Transactional(readOnly = true)
    public TransactionResponse getFullTransaction(Long transactionId) {
        // 1. 取得單一主檔
        TransactionRecordEntity transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此交易單號: " + transactionId));

        // 2. 轉換主檔基本資料
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setMemberId(transaction.getMemberId());
        response.setTransactionTime(transaction.getTransactionTime());
        response.setLashArtist(transaction.getLashArtist());
        response.setAmountBeforeDiscount(transaction.getAmountBeforeDiscount());
        response.setAmountAfterDiscount(transaction.getAmountAfterDiscount());
        response.setDiscountType(transaction.getDiscountType());
        response.setDiscountRate(transaction.getDiscountRate());
        response.setPaymentMethod(transaction.getPaymentMethod());
        response.setRemark(transaction.getRemark());

        // 3. 轉換操作項目 (第一層 List)
        // 確保 response.setOperationItems 接收的是 List<OperationItemResponse>
        response.setOperationItems(transaction.getOperationItems().stream().map(op -> {
            OperationItemResponse opDto = new OperationItemResponse();
            opDto.setOperationItemId(op.getOperationItemId());
            // opDto.setOperationName(op.getOperationName()); // 假設有此欄位
            
            // 4. 轉換睫毛區域明細 (第二層 List)
            opDto.setEyelashAreaDetails(op.getAreaDetails().stream().map(area -> {
                EyelashAreaDetailResponse areaDto = new EyelashAreaDetailResponse();
                areaDto.setEyelashAreaDetailId(area.getEyelashAreaDetailId());
                areaDto.setPosition(area.getPosition());
                areaDto.setLashCount(area.getLashCount());
                areaDto.setLashLengths(area.getLashLengths());
                areaDto.setLashCurls(area.getLashCurls());
                return areaDto;
            }).toList());
            
            return opDto;
        }).toList());

        // 5. 轉換交易明細 (另一條 List 關聯)
        response.setTransactionDetails(transaction.getTransactionDetails().stream().map(detail -> {
        	return new TransactionDetailResponse(
        	        detail.getTransactionDetailId(),
        	        detail.getItemName(),
        	        detail.getItemPrice(),
        	        detail.getQuantity()
        	    );
        }).toList());

        return response;
    }
    
    @Transactional
    public void deleteTransaction(Long transactionId) {
        if (!transactionRepo.existsById(transactionId)) {
            throw new ResourceNotFoundException("刪除失敗，找不到交易單號為 " + transactionId + " 的紀錄");
        }
        
        transactionRepo.deleteById(transactionId);
    }
}
