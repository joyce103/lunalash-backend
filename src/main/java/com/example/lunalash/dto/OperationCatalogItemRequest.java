package com.example.lunalash.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OperationCatalogItemRequest {

    @NotBlank(message = "項目名稱不能為空")
    @Size(max = 100, message = "項目名稱長度不能超過 100 字")
    private String name;

    @NotNull(message = "操作時間（分鐘）不能為空")
    @Min(value = 1, message = "操作時間必須大於 0 分鐘")
    private Integer durationMinutes;

    @NotNull(message = "排序不能為空")
    private Integer sortOrder;

    @NotNull(message = "是否啟用不能為空")
    private Boolean isActive;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
