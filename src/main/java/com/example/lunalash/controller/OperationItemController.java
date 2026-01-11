package com.example.lunalash.controller;

import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.repository.OperationItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operation_items")
public class OperationItemController {

    private final OperationItemRepository operationItemRepository;

    public OperationItemController(OperationItemRepository operationItemRepository) {
        this.operationItemRepository = operationItemRepository;
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
}
