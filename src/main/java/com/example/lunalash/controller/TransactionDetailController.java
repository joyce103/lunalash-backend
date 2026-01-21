package com.example.lunalash.controller;

import com.example.lunalash.service.TransactionDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactionDetails")
public class TransactionDetailController {

    private final TransactionDetailService service;

    public TransactionDetailController(TransactionDetailService service) {
        this.service = service;
    }

    /**
     * 刪除單筆交易明細
     */
    @DeleteMapping("/{detailId}")
    public ResponseEntity<?> deleteDetail(@PathVariable Long detailId) {
        service.deleteDetail(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 刪除交易底下所有明細
     */
    @DeleteMapping("/transaction/{transactionId}")
    public ResponseEntity<?> deleteAllByTransaction(
            @PathVariable Long transactionId
    ) {
        service.deleteAllByTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}
