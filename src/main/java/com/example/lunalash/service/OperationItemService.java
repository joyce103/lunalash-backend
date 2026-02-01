package com.example.lunalash.service;

import com.example.lunalash.dto.EyelashAreaDetailResponse;
import com.example.lunalash.dto.OperationItemRequest;
import com.example.lunalash.dto.OperationItemResponse;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.repository.OperationItemRepository;
import com.example.lunalash.repository.TransactionRecordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class OperationItemService {

    private final OperationItemRepository operationItemRepo;
    private final EyelashAreaDetailRepository eyelashAreaDetailRepo;
    private final TransactionRecordRepository transactionRecordRepo;

    public OperationItemService(
    		OperationItemRepository operationItemRepo,
    		EyelashAreaDetailRepository eyelashAreaDetailRepo,
    		TransactionRecordRepository transactionRecordRepo
    ) {
        this.operationItemRepo = operationItemRepo;
        this.eyelashAreaDetailRepo = eyelashAreaDetailRepo;
        this.transactionRecordRepo = transactionRecordRepo;
    }

    @Transactional
    public Long createOperationItem(OperationItemRequest request) {
    	// 尋找對應的交易，找不到就噴 404
        TransactionRecordEntity transaction = transactionRecordRepo.findById(request.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到交易單號：" + request.getTransactionId()));

        // ️建立操作項目
        OperationItemEntity operationItem = new OperationItemEntity();
        operationItem.setTransaction(transaction); // 建立關聯
        operationItem.setOperationName(request.getOperationName());
        operationItem.setTotalLashCount(request.getTotalLashCount());
        operationItem.setStyle(request.getStyle());
        operationItem.setThickness(request.getThickness());
        operationItem.setBrand(request.getBrand());
        operationItem.setCategory(request.getCategory());
        operationItem.setGlueType(request.getGlueType());
        operationItem.setRemark(request.getRemark());

        // 先操作項目，讓 operationItemId 自動生成
        operationItem = operationItemRepo.save(operationItem);

        // 建立區域項目
        List<EyelashAreaDetailEntity> areaItems = new ArrayList<>();
        for (var itemReq : request.getEyelashAreaDetail()) {
        	EyelashAreaDetailEntity item = new EyelashAreaDetailEntity();
            item.setPosition(itemReq.getPosition());
            item.setLashCount(itemReq.getLashCount());
            item.setLashLengths(itemReq.getLashLengths());
            item.setLashCurls(itemReq.getLashCurls());

            item.setOperationItem(operationItem);
            areaItems.add(item);
        }

        // 一次存所有區域項目
        eyelashAreaDetailRepo.saveAll(areaItems);

        return operationItem.getOperationItemId();
    }

    public List<OperationItemResponse> getOperationItemsByTransactionId(Long transactionId) {
        List<OperationItemEntity> entities = operationItemRepo.findByTransaction_TransactionId(transactionId);
        
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("找不到交易單號為 " + transactionId + " 的操作項目");
        }

        // 將 Entity 轉換為 Response DTO
        return entities.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private OperationItemResponse convertToResponse(OperationItemEntity entity) {
        OperationItemResponse resp = new OperationItemResponse();
        resp.setOperationItemId(entity.getOperationItemId());
        resp.setOperationName(entity.getOperationName());
        resp.setTotalLashCount(entity.getTotalLashCount());
        resp.setStyle(entity.getStyle());
        resp.setThickness(entity.getThickness());
        resp.setBrand(entity.getBrand());
        resp.setCategory(entity.getCategory());
        resp.setGlueType(entity.getGlueType());
        resp.setRemark(entity.getRemark());

        // 轉換內層區域資料 (對應到 OperationItemEntity 中 getAreaDetails() 方法)
        if (entity.getAreaDetails() != null) {
            List<EyelashAreaDetailResponse> details = entity.getAreaDetails().stream().map(detailEntity -> {
                EyelashAreaDetailResponse detailResp = new EyelashAreaDetailResponse();
                detailResp.setPosition(detailEntity.getPosition());
                detailResp.setLashCount(detailEntity.getLashCount());
                detailResp.setLashLengths(detailEntity.getLashLengths());
                detailResp.setLashCurls(detailEntity.getLashCurls());
                return detailResp;
            }).collect(Collectors.toList());
            resp.setEyelashAreaDetails(details);
        }
        return resp;
    }
}
