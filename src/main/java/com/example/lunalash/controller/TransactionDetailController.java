package com.example.lunalash.controller;

import com.example.lunalash.dto.TransactionDetailRequest;
import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.service.TransactionDetailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactionDetails")
@Tag(name = "交易明細")
public class TransactionDetailController {

    private final TransactionDetailService service;

    public TransactionDetailController(TransactionDetailService service) {
        this.service = service;
    }
    
    @Operation(summary = "查詢單筆交易明細")
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionDetail(
    		@Parameter(description="交易單號")
    		@PathVariable Long transactionId
    ) {
    	List<TransactionDetailResponse> response =
                service.getTransactionDetail(transactionId)
                       .stream()
                       .map(d -> new TransactionDetailResponse(
                               d.getTransactionDetailId(),
                               d.getItemName(),
                               d.getItemPrice(),
                               d.getQuantity()
                       ))
                       .toList();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "新增交易明細")
    @PostMapping("/addDetails")
    public ResponseEntity<TransactionDetailResponse> createTransactionDetail(
            @RequestBody TransactionDetailRequest request
    ) {
        TransactionDetailEntity savedDetail = service.createTransactionDetail(request);

        TransactionDetailResponse response = new TransactionDetailResponse(
                savedDetail.getTransactionDetailId(),
                savedDetail.getItemName(),
                savedDetail.getItemPrice(),
                savedDetail.getQuantity()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "刪除單筆交易明細")
    @DeleteMapping("/{detailId}")
    public ResponseEntity<?> deleteDetail(@Parameter(description="交易明細編號")@PathVariable Long detailId) {
        service.deleteDetail(detailId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "刪除交易下所有明細")
    @DeleteMapping("/transaction/{transactionId}")
    public ResponseEntity<?> deleteAllByTransaction(
    		@Parameter(description="交易單號")
            @PathVariable Long transactionId
    ) {
        service.deleteAllByTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}
