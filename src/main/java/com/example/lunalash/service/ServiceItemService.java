package com.example.lunalash.service;

import com.example.lunalash.dto.ServiceItemRequest;
import com.example.lunalash.dto.ServiceItemResponse;
import com.example.lunalash.entity.ServiceItemEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemService {

    private final ServiceItemRepository repo;

    public ServiceItemService(ServiceItemRepository repo) {
        this.repo = repo;
    }

    // 訪客預約表單用：只給啟用中的服務
    public List<ServiceItemResponse> getActiveServices() {
        return repo.findByIsActiveTrueOrderBySortOrderAsc().stream().map(ServiceItemResponse::new).toList();
    }

    // 後台管理用：全部服務都要看到
    public List<ServiceItemResponse> getAllServices() {
        return repo.findAllByOrderBySortOrderAsc().stream().map(ServiceItemResponse::new).toList();
    }

    public ServiceItemResponse create(ServiceItemRequest request) {
        ServiceItemEntity entity = new ServiceItemEntity();
        applyRequest(entity, request);
        return new ServiceItemResponse(repo.save(entity));
    }

    public ServiceItemResponse update(Long serviceItemId, ServiceItemRequest request) {
        ServiceItemEntity entity = repo.findById(serviceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此服務項目"));
        applyRequest(entity, request);
        return new ServiceItemResponse(repo.save(entity));
    }

    public void delete(Long serviceItemId) {
        ServiceItemEntity entity = repo.findById(serviceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此服務項目"));
        repo.delete(entity);
    }

    private void applyRequest(ServiceItemEntity entity, ServiceItemRequest request) {
        entity.setName(request.getName().trim());
        entity.setSortOrder(request.getSortOrder());
        entity.setIsActive(request.getIsActive());
    }
}
