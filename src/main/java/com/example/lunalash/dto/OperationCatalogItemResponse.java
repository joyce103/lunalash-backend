package com.example.lunalash.dto;

import com.example.lunalash.entity.OperationCatalogItemEntity;

public class OperationCatalogItemResponse {

    private Long operationCatalogItemId;
    private String name;
    private Integer durationMinutes;
    private Integer sortOrder;
    private Boolean isActive;

    public OperationCatalogItemResponse(OperationCatalogItemEntity entity) {
        this.operationCatalogItemId = entity.getOperationCatalogItemId();
        this.name = entity.getName();
        this.durationMinutes = entity.getDurationMinutes();
        this.sortOrder = entity.getSortOrder();
        this.isActive = entity.getIsActive();
    }

    public Long getOperationCatalogItemId() { return operationCatalogItemId; }
    public String getName() { return name; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Integer getSortOrder() { return sortOrder; }
    public Boolean getIsActive() { return isActive; }
}
