package com.example.lunalash.service;

import com.example.lunalash.dto.OperationItemRequest;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.repository.OperationItemRepository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class OperationItemService {

    private final OperationItemRepository operationItemRepo;
    private final EyelashAreaDetailRepository eyelashAreaDetailRepo;

    public OperationItemService(
    		OperationItemRepository operationItemRepo,
    		EyelashAreaDetailRepository eyelashAreaDetailRepo
    ) {
        this.operationItemRepo = operationItemRepo;
        this.eyelashAreaDetailRepo = eyelashAreaDetailRepo;
    }

    @Transactional
    public Long createOperationItem(OperationItemRequest request) {

        // 1️⃣ 建立交易
        OperationItemEntity operationItem = new OperationItemEntity();
        operationItem.setOperationName(request.operationName);
        operationItem.setTotalLashCount(request.totalLashCount);
        operationItem.setStyle(request.style);
        operationItem.setThickness(request.thickness);
        operationItem.setBrand(request.brand);
        operationItem.setCategory(request.category);
        operationItem.setGlueType(request.glueType);
        operationItem.setRemark(request.remark);


        // 先操作項目，讓 operationItemId 自動生成
        operationItem = operationItemRepo.save(operationItem);

        // 2️⃣ 建立操作項目
        List<EyelashAreaDetailEntity> areaItems = new ArrayList<>();
        for (var itemReq : request.eyelashAreaDetail) {
        	EyelashAreaDetailEntity item = new EyelashAreaDetailEntity();
            item.setPosition(itemReq.position);
            item.setLashCount(itemReq.lashCount);
            item.setLashLengths(itemReq.lashLengths);
            item.setLashCurls(itemReq.lashCurls);

            item.setOperationItem(operationItem);
            areaItems.add(item);
        }

        // 一次存所有操作項目
        eyelashAreaDetailRepo.saveAll(areaItems);

        return operationItem.getOperationItemId();
    }
}
