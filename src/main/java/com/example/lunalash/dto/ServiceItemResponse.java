package com.example.lunalash.dto;

import com.example.lunalash.entity.ServiceItemEntity;

public class ServiceItemResponse {

    private Long serviceItemId;
    private String name;
    private Integer sortOrder;
    private Boolean isActive;

    public ServiceItemResponse(ServiceItemEntity entity) {
        this.serviceItemId = entity.getServiceItemId();
        this.name = entity.getName();
        this.sortOrder = entity.getSortOrder();
        this.isActive = entity.getIsActive();
    }

    public Long getServiceItemId() { return serviceItemId; }
    public String getName() { return name; }
    public Integer getSortOrder() { return sortOrder; }
    public Boolean getIsActive() { return isActive; }
}
