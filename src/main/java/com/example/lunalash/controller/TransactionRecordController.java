package com.example.lunalash.controller;

import com.example.lunalash.dto.TransactionCreateRequest;
import com.example.lunalash.entity.TransactionRecordEntity;
import com.example.lunalash.repository.TransactionRecordRepository;
import com.example.lunalash.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
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

    // ✅ 新增交易（含多筆操作項目）
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody TransactionCreateRequest request
    ) {
        Long transactionId = transactionService.createTransaction(request);
        return ResponseEntity.ok(transactionId);
    }

    // 查詢會員的交易紀錄
    @GetMapping("/member/{memberId}")
    public List<TransactionRecordEntity> getByMember(
            @PathVariable Long memberId
    ) {
        return repository.findByMemberId(memberId);
    }

    // 查詢單筆交易
    @GetMapping("/{transactionId}")
    public TransactionRecordEntity getOne(
            @PathVariable Long transactionId
    ) {
        return repository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("交易不存在"));
    }

    // 刪除交易
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long transactionId
    ) {
        if (!repository.existsById(transactionId)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(transactionId);
        return ResponseEntity.noContent().build(); // 204
    }
}
