package com.example.lunalash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServiceItemRequest {

    @NotBlank(message = "服務名稱不能為空")
    @Size(max = 100, message = "服務名稱長度不能超過 100 字")
    private String name;

    @NotNull(message = "排序不能為空")
    private Integer sortOrder;

    @NotNull(message = "是否啟用不能為空")
    private Boolean isActive;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
