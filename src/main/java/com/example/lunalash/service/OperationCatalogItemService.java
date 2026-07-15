package com.example.lunalash.service;

import com.example.lunalash.dto.OperationCatalogItemRequest;
import com.example.lunalash.dto.OperationCatalogItemResponse;
import com.example.lunalash.entity.OperationCatalogItemEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.OperationCatalogItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationCatalogItemService {

    private final OperationCatalogItemRepository repo;

    public OperationCatalogItemService(OperationCatalogItemRepository repo) {
        this.repo = repo;
    }

    // 訪客預約表單用：只給啟用中的項目
    public List<OperationCatalogItemResponse> getActiveItems() {
        return repo.findByIsActiveTrueOrderBySortOrderAsc().stream().map(OperationCatalogItemResponse::new).toList();
    }

    // 後台管理用：全部項目都要看到
    public List<OperationCatalogItemResponse> getAllItems() {
        return repo.findAllByOrderBySortOrderAsc().stream().map(OperationCatalogItemResponse::new).toList();
    }

    public OperationCatalogItemResponse create(OperationCatalogItemRequest request) {
        OperationCatalogItemEntity entity = new OperationCatalogItemEntity();
        applyRequest(entity, request);
        return new OperationCatalogItemResponse(repo.save(entity));
    }

    public OperationCatalogItemResponse update(Long id, OperationCatalogItemRequest request) {
        OperationCatalogItemEntity entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("查無此操作項目"));
        applyRequest(entity, request);
        return new OperationCatalogItemResponse(repo.save(entity));
    }

    public void delete(Long id) {
        OperationCatalogItemEntity entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("查無此操作項目"));
        repo.delete(entity);
    }

    // 查詢時段狀態用：把選擇的操作項目 id 換算成時間加總 (分鐘)。這裡只是預覽用途，
    // 找不到的 id 直接忽略不計，真正送出預約時 AppointmentService 還會再做一次嚴格驗證
    public int sumDurationMinutes(List<Long> operationItemIds) {
        return repo.findAllById(operationItemIds).stream()
                .mapToInt(OperationCatalogItemEntity::getDurationMinutes)
                .sum();
    }

    private void applyRequest(OperationCatalogItemEntity entity, OperationCatalogItemRequest request) {
        entity.setName(request.getName().trim());
        entity.setDurationMinutes(request.getDurationMinutes());
        entity.setSortOrder(request.getSortOrder());
        entity.setIsActive(request.getIsActive());
    }
}
