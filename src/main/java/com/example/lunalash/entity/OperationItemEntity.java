package com.example.lunalash.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "operation_item",
    indexes = {
        @Index(name = "idx_operation_transaction", columnList = "transaction_id")
    }
)
public class OperationItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_item_id")
    private Long operationItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecordEntity transaction;

    @Column(name = "operation_name", nullable = false, length = 10)
    private String operationName;

    @Column(name = "total_lash_count", nullable = false)
    private Integer totalLashCount; // SMALLINT → Integer（安全、不踩雷）

    @Column(nullable = false, length = 10)
    private String style;

    @Column(nullable = false, length = 10)
    private String thickness;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "glue_type", nullable = false, length = 50)
    private String glueType;

    @Column(columnDefinition = "TEXT")
    private String remark;

    // ===== Getter / Setter =====

    public Long getOperationItemId() {
        return operationItemId;
    }

    public void setOperationItemId(Long operationItemId) {
        this.operationItemId = operationItemId;
    }
    
    public TransactionRecordEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionRecordEntity transaction) {
        this.transaction = transaction;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Integer getTotalLashCount() {
        return totalLashCount;
    }

    public void setTotalLashCount(Integer totalLashCount) {
        this.totalLashCount = totalLashCount;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGlueType() {
        return glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
