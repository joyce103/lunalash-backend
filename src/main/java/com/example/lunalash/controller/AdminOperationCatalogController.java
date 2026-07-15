package com.example.lunalash.controller;

import com.example.lunalash.dto.OperationCatalogItemRequest;
import com.example.lunalash.dto.OperationCatalogItemResponse;
import com.example.lunalash.service.OperationCatalogItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 需要登入才能呼叫，管理操作項目的 CRUD 與排序/啟用狀態
@RestController
@RequestMapping("/api/admin/operation-catalog")
@Tag(name = "預約-操作項目管理 (後台)")
public class AdminOperationCatalogController {

    private final OperationCatalogItemService service;

    public AdminOperationCatalogController(OperationCatalogItemService service) {
        this.service = service;
    }

    @Operation(summary = "查詢所有操作項目 (含停用)")
    @GetMapping
    public List<OperationCatalogItemResponse> getAllItems() {
        return service.getAllItems();
    }

    @Operation(summary = "新增操作項目")
    @PostMapping
    public OperationCatalogItemResponse create(@Valid @RequestBody OperationCatalogItemRequest request) {
        return service.create(request);
    }

    @Operation(summary = "修改操作項目")
    @PutMapping("/{operationCatalogItemId}")
    public OperationCatalogItemResponse update(@PathVariable Long operationCatalogItemId, @Valid @RequestBody OperationCatalogItemRequest request) {
        return service.update(operationCatalogItemId, request);
    }

    @Operation(summary = "刪除操作項目")
    @DeleteMapping("/{operationCatalogItemId}")
    public void delete(@PathVariable Long operationCatalogItemId) {
        service.delete(operationCatalogItemId);
    }
}
