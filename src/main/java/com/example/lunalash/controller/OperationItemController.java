package com.example.lunalash.controller;

import com.example.lunalash.dto.OperationItemRequest;
import com.example.lunalash.dto.OperationItemResponse;
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

	private final OperationItemService operationItemService;

    public OperationItemController(OperationItemService operationItemService) {
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
    
    @Operation(summary = "查詢交易操作項目")
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<OperationItemResponse>> getByTransactionId(
            @Parameter(description="交易單號") @PathVariable Long transactionId) {
        
        List<OperationItemResponse> results = operationItemService.getOperationItemsByTransactionId(transactionId);
        return ResponseEntity.ok(results);
    }
    
}
