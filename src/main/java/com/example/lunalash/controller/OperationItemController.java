package com.example.lunalash.controller;

import com.example.lunalash.dto.OperationItemRequest;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.repository.OperationItemRepository;
import com.example.lunalash.service.OperationItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operationItems")
@Tag(name = "操作項目")
public class OperationItemController {

    private final OperationItemRepository operationItemRepository;
    private final OperationItemService operationItemService;

    public OperationItemController(OperationItemRepository operationItemRepository, OperationItemService operationItemService) {
        this.operationItemRepository = operationItemRepository;
        this.operationItemService = operationItemService;
    }

    @Operation(summary = "新增操作項目")
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody OperationItemRequest request
    ) {
        Long operationItemId = operationItemService.createOperationItem(request);
        return ResponseEntity.ok(operationItemId);
    }

    @Operation(summary = "查詢操作項目")
    @GetMapping("/transaction/{transactionId}")
    public List<OperationItemEntity> getByTransactionId(
    		@Parameter(description="交易單號")
            @PathVariable Long transactionId
    ) {
        return operationItemRepository.findByTransaction_TransactionId(transactionId);
    }
    

    // 查詢某操作項目下的所有區域資料
//    @GetMapping("/{id}/areas")
//    public List<EyelashAreaDetailEntity> getAreas(@PathVariable Long id) {
//        return eyelashAreaRepository.findByOperationItemId(id);
//    }
}
