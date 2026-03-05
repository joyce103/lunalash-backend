package com.example.lunalash.dto;

import java.util.List;

public class BatchCreateAreaRequest {
    
    private Long operationItemId;
    private List<AreaItem> areas; 

    // --- 外層 Getter & Setter ---
    public Long getOperationItemId() { return operationItemId; }
    public void setOperationItemId(Long operationItemId) { this.operationItemId = operationItemId; }
    public List<AreaItem> getAreas() { return areas; }
    public void setAreas(List<AreaItem> areas) { this.areas = areas; }

    // 區域明細
    public static class AreaItem {
        private String position;
        private Integer lashCount;
        private List<Integer> lashLengths;
        private List<String> lashCurls;

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public Integer getLashCount() { return lashCount; }
        public void setLashCount(Integer lashCount) { this.lashCount = lashCount; }
        public List<Integer> getLashLengths() { return lashLengths; }
        public void setLashLengths(List<Integer> lashLengths) { this.lashLengths = lashLengths; }
        public List<String> getLashCurls() { return lashCurls; }
        public void setLashCurls(List<String> lashCurls) { this.lashCurls = lashCurls; }
    }
}