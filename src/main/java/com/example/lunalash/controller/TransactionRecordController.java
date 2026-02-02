package com.example.lunalash.controller;

import com.example.lunalash.dto.TransactionCreateRequest;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.repository.TransactionRecordRepository;
import com.example.lunalash.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "交易內容")
public class TransactionRecordController {

    private final TransactionRecordRepository repository;
    private final TransactionService transactionService;

    public TransactionRecordController(
            TransactionRecordRepository repository,
            TransactionService transactionService
    ) {
        this.repository = repository;
        this.transactionService = transactionService;
    }

    @Operation(summary = "新增交易（含多筆操作項目及交易明細）")
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody TransactionCreateRequest request
    ) {
        Long transactionId = transactionService.createTransaction(request);
        return ResponseEntity.ok(transactionId);
    }

    @Operation(summary = "查詢會員交易紀錄")
    @GetMapping("/member/{memberId}")
    public List<TransactionRecordEntity> getTransactionByMember(
            @Parameter(description="會員ID")
            @PathVariable Long memberId
    ) {
        return transactionService.getTransactionsByMemberId(memberId);
    }

    @Operation(summary = "查詢單筆交易")
    @GetMapping("/{transactionId}")
    public List<TransactionRecordEntity> getTransaction(
    		@Parameter(description="交易單號")
            @PathVariable Long transactionId
    ) {
        return transactionService.getTransactionByTransactionId(transactionId);

    }

    @Operation(summary = "刪除單筆交易")
    @DeleteMapping("/{transactionId}")
    public void delete(
            @Parameter(description="交易單號")
            @PathVariable Long transactionId
    ) {
        transactionService.deleteTransaction(transactionId);
    }
}
