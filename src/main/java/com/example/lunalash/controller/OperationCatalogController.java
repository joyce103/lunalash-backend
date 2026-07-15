package com.example.lunalash.controller;

import com.example.lunalash.dto.OperationCatalogItemResponse;
import com.example.lunalash.service.OperationCatalogItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 訪客預約表單用，完全公開，不需要登入
@RestController
@RequestMapping("/api/operation-catalog")
@Tag(name = "預約-操作項目 (公開)")
public class OperationCatalogController {

    private final OperationCatalogItemService service;

    public OperationCatalogController(OperationCatalogItemService service) {
        this.service = service;
    }

    @Operation(summary = "查詢所有啟用中的操作項目")
    @GetMapping
    public List<OperationCatalogItemResponse> getActiveItems() {
        return service.getActiveItems();
    }
}
