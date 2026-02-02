package com.example.lunalash.controller;

import com.example.lunalash.dto.TransactionDetailRequest;
import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.service.TransactionDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<TransactionDetailResponse> getTransactionDetail(
            @Parameter(description="交易單號") @PathVariable Long transactionId
    ) {
        return service.getTransactionDetailsByTransactionId(transactionId);
    }
    
    @Operation(summary = "新增交易明細")
    @PostMapping("/addDetails")
    public TransactionDetailResponse createTransactionDetail(
            @RequestBody TransactionDetailRequest request
    ) {
        return service.createTransactionDetail(request);
    }

    @Operation(summary = "刪除單筆交易明細")
    @DeleteMapping("/{detailId}")
    public void deleteDetail(
            @Parameter(description="交易明細編號") @PathVariable Long detailId
    ) {
        service.deleteDetail(detailId);
    }

    @Operation(summary = "刪除交易下所有明細")
    @DeleteMapping("/transaction/{transactionId}")
    public void deleteAllByTransaction(
            @Parameter(description="交易單號") @PathVariable Long transactionId
    ) {
        service.deleteAllByTransaction(transactionId);
    }
}