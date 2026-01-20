package com.example.lunalash.dto;

import java.util.List;

public class OperationItemRequest {

    public String operationName;
    public Integer totalLashCount;
    public String style;
    public String thickness;
    public String brand;
    public String category;
    public String glueType;
    public String remark;
    
    public List<EyelashAreaDetailRequest> eyelashAreaDetail;
}
