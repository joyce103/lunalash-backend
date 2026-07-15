package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 預約系統用的「操作項目」目錄：訪客預約時可複選，依選擇項目的時間加總決定要占用幾個時段。
// 跟 ServiceItemEntity（原本單選的服務項目，目前預約表單已改用這張表）是不同概念，故意分開避免混用。
@Entity
@Table(name = "operation_catalog_item")
public class OperationCatalogItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_catalog_item_id")
    private Long operationCatalogItemId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    // ===== Getter / Setter =====

    public Long getOperationCatalogItemId() { return operationCatalogItemId; }
    public void setOperationCatalogItemId(Long operationCatalogItemId) { this.operationCatalogItemId = operationCatalogItemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
