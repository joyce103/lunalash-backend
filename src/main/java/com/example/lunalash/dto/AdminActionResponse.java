package com.example.lunalash.dto;

// 新增/刪除管理員帳號的統一回應：
// 最高管理員操作會直接執行 (EXECUTED)，一般管理員操作則會建立一筆待審核申請 (PENDING_APPROVAL)
public class AdminActionResponse {

    private String status; // EXECUTED / PENDING_APPROVAL
    private AdminResponse admin; // 只有 EXECUTED 且是新增帳號時才會有值
    private Long requestId; // 只有 PENDING_APPROVAL 才會有值

    public static AdminActionResponse executed(AdminResponse admin) {
        AdminActionResponse response = new AdminActionResponse();
        response.status = "EXECUTED";
        response.admin = admin;
        return response;
    }

    public static AdminActionResponse pending(Long requestId) {
        AdminActionResponse response = new AdminActionResponse();
        response.status = "PENDING_APPROVAL";
        response.requestId = requestId;
        return response;
    }

    public String getStatus() { return status; }
    public AdminResponse getAdmin() { return admin; }
    public Long getRequestId() { return requestId; }
}
