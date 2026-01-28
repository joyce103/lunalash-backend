package com.example.lunalash.dto;

import java.util.List;

public class OperationItemRequest {

	private Long transactionId;
	private String operationName;
    private Integer totalLashCount;
    private String style;
    private String thickness;
    private String brand;
    private String category;
    private String glueType;
    private String remark;
    private List<EyelashAreaDetailRequest> eyelashAreaDetail;

    // --- Getter & Setter ---
    
    public Long getTransactionId() {
    	return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
    	this.transactionId = transactionId;
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

    public List<EyelashAreaDetailRequest> getEyelashAreaDetail() {
        return eyelashAreaDetail;
    }

    public void setEyelashAreaDetail(List<EyelashAreaDetailRequest> eyelashAreaDetail) {
        this.eyelashAreaDetail = eyelashAreaDetail;
    }
}