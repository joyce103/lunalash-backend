package com.example.lunalash.controller;

import com.example.lunalash.dto.TransactionDetailRequest;
import com.example.lunalash.dto.TransactionDetailResponse;
import com.example.lunalash.entity.TransactionDetailEntity;
import com.example.lunalash.service.TransactionDetailService;

import java.util.List;

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
     * 查詢交易明細
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionDetail(
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
    
    /**
     * 新增交易明細
     */
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
