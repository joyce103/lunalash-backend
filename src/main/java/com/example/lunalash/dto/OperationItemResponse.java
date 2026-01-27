package com.example.lunalash.dto;

import lombok.Data;
import java.util.List;

@Data
public class OperationItemResponse {
    private Long operationItemId;
    private String operationName;
    private Integer totalLashCount;
    private String style;
    private String thickness;
    private String brand;
    private String category;
    private String glueType;
    private String remark;
    private List<EyelashAreaDetailResponse> eyelashAreaDetails;

    // Getter & Setter
    public Long getOperationItemId() { return operationItemId; }
    public void setOperationItemId(Long operationItemId) { this.operationItemId = operationItemId; }

    public String getOperationName() { return operationName; }
    public void setOperationName(String operationName) { this.operationName = operationName; }

    public Integer getTotalLashCount() { return totalLashCount; }
    public void setTotalLashCount(Integer totalLashCount) { this.totalLashCount = totalLashCount; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public String getThickness() { return thickness; }
    public void setThickness(String thickness) { this.thickness = thickness; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGlueType() { return glueType; }
    public void setGlueType(String glueType) { this.glueType = glueType; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<EyelashAreaDetailResponse> getEyelashAreaDetails() { return eyelashAreaDetails; }
    public void setEyelashAreaDetails(List<EyelashAreaDetailResponse> eyelashAreaDetails) { 
        this.eyelashAreaDetails = eyelashAreaDetails; 
    }
}