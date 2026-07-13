package com.example.lunalash.controller;

import com.example.lunalash.dto.ServiceItemRequest;
import com.example.lunalash.dto.ServiceItemResponse;
import com.example.lunalash.service.ServiceItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 需要登入才能呼叫，管理服務項目的 CRUD 與排序/啟用狀態
@RestController
@RequestMapping("/api/admin/services")
@Tag(name = "預約-服務項目管理 (後台)")
public class AdminServiceController {

    private final ServiceItemService serviceItemService;

    public AdminServiceController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @Operation(summary = "查詢所有服務項目 (含停用)")
    @GetMapping
    public List<ServiceItemResponse> getAllServices() {
        return serviceItemService.getAllServices();
    }

    @Operation(summary = "新增服務項目")
    @PostMapping
    public ServiceItemResponse create(@Valid @RequestBody ServiceItemRequest request) {
        return serviceItemService.create(request);
    }

    @Operation(summary = "修改服務項目")
    @PutMapping("/{serviceItemId}")
    public ServiceItemResponse update(@PathVariable Long serviceItemId, @Valid @RequestBody ServiceItemRequest request) {
        return serviceItemService.update(serviceItemId, request);
    }

    @Operation(summary = "刪除服務項目")
    @DeleteMapping("/{serviceItemId}")
    public void delete(@PathVariable Long serviceItemId) {
        serviceItemService.delete(serviceItemId);
    }
}
