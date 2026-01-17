package com.example.lunalash.controller;

import com.example.lunalash.dto.EyelashAreaDetailRequest;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.repository.OperationItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operation_items")
public class OperationItemController {

    private final OperationItemRepository operationItemRepository;
    private final EyelashAreaDetailRepository eyelashAreaRepository;

    public OperationItemController(OperationItemRepository operationItemRepository, EyelashAreaDetailRepository eyelashAreaRepository) {
        this.operationItemRepository = operationItemRepository;
        this.eyelashAreaRepository = eyelashAreaRepository;
    }

    // 新增操作項目
    @PostMapping
    public OperationItemEntity create(@RequestBody OperationItemEntity item) {
        return operationItemRepository.save(item);
    }

    // 依交易單號取得操作項目
    @GetMapping("/transaction/{transactionId}")
    public List<OperationItemEntity> getByTransactionId(
            @PathVariable Long transactionId
    ) {
        return operationItemRepository.findByTransaction_TransactionId(transactionId);
    }
    
    // 新增多筆區域資料
    @PostMapping("/{id}/areas")
    public List<EyelashAreaDetailEntity> addAreas(
            @PathVariable Long id,
            @RequestBody List<EyelashAreaDetailRequest> areas
    ) {
        OperationItemEntity operationItem = operationItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("操作項目不存在"));

        List<EyelashAreaDetailEntity> entities = areas.stream().map(dto -> {
            EyelashAreaDetailEntity entity = new EyelashAreaDetailEntity();
            entity.setPosition(dto.getPosition());
            entity.setLashCount(dto.getLashCount());
            entity.setLashLengths(dto.getLashLengths());
            entity.setLashCurls(dto.getLashCurls());
            entity.setOperationItem(operationItem);
            return entity;
        }).collect(Collectors.toList());

        return eyelashAreaRepository.saveAll(entities);
    }

    // 查詢某操作項目下的所有區域資料
//    @GetMapping("/{id}/areas")
//    public List<EyelashAreaDetailEntity> getAreas(@PathVariable Long id) {
//        return eyelashAreaRepository.findByOperationItemId(id);
//    }
}
