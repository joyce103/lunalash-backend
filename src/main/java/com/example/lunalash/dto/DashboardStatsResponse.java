package com.example.lunalash.dto;

public class DashboardStatsResponse {

    private long todayCount;
    private long pendingCount;
    private long monthCount;
    private long approvedCount;

    public DashboardStatsResponse(long todayCount, long pendingCount, long monthCount, long approvedCount) {
        this.todayCount = todayCount;
        this.pendingCount = pendingCount;
        this.monthCount = monthCount;
        this.approvedCount = approvedCount;
    }

    public long getTodayCount() { return todayCount; }
    public long getPendingCount() { return pendingCount; }
    public long getMonthCount() { return monthCount; }
    public long getApprovedCount() { return approvedCount; }
}
