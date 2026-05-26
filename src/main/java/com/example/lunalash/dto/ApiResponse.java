package com.example.lunalash.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 統一回傳格式")
public class ApiResponse<T> {
    
    private int resultCode;
    private String resultMsg;
    private String runTime;
    private T resultData; // T 會根據傳入的型別動態改變

    public static <T> ApiResponse<T> success(T resultData, long startTime) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResultCode(0);
        response.setResultMsg("作業成功");
        response.setResultData(resultData);
        long duration = System.currentTimeMillis() - startTime;
        response.setRunTime(duration + "ms");
        return response;
    }
    
    public static <T> ApiResponse<T> fail(int code, String msg, long startMillis) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResultCode(code);
        response.setResultMsg(msg);
        long duration = System.currentTimeMillis() - startMillis;
        response.setRunTime(duration + "ms");
        return response;
    }

    // --- Getter & Setter ---

    public int getResultCode() { return resultCode; }
    public void setResultCode(int resultCode) { this.resultCode = resultCode; }

    public String getResultMsg() { return resultMsg; }
    public void setResultMsg(String resultMsg) { this.resultMsg = resultMsg; }

    public String getRunTime() { return runTime; }
    public void setRunTime(String runTime) { this.runTime = runTime; }

    public T getResultData() { return resultData; }
    public void setResultData(T resultData) { this.resultData = resultData; }
}