package com.example.lunalash.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.lunalash.dto.TransactionCreateRequest;
import com.example.lunalash.service.TransactionService;

@RestControllerAdvice
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Long create(@RequestBody TransactionCreateRequest request) {
        return service.createTransaction(request);
    }
}
