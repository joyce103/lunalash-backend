package com.example.lunalash.dto;

import java.util.List;

public class EyelashAreaDetailRequest {
    private Long eyelashAreaDetailId;
    private String position;
    private Integer lashCount;
    private List<Integer> lashLengths;
    private List<String> lashCurls;
    private Long operationItemId;
    
    private List<EyelashAreaDetailRequest> areas; 

    // --- Getter & Setter ---
 
    public List<EyelashAreaDetailRequest> getAreas() { return areas; }
    public void setAreas(List<EyelashAreaDetailRequest> areas) { this.areas = areas; }


    public Long getEyelashAreaDetailId() {
        return eyelashAreaDetailId;
    }

    public void setEyelashAreaDetailId(Long eyelashAreaDetailId) {
        this.eyelashAreaDetailId = eyelashAreaDetailId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getLashCount() {
        return lashCount;
    }

    public void setLashCount(Integer lashCount) {
        this.lashCount = lashCount;
    }

    public List<Integer> getLashLengths() {
        return lashLengths;
    }

    public void setLashLengths(List<Integer> lashLengths) {
        this.lashLengths = lashLengths;
    }

    public List<String> getLashCurls() {
        return lashCurls;
    }

    public void setLashCurls(List<String> lashCurls) {
        this.lashCurls = lashCurls;
    }

    public Long getOperationItemId() {
        return operationItemId;
    }

    public void setOperationItemId(Long operationItemId) {
        this.operationItemId = operationItemId;
    }
}