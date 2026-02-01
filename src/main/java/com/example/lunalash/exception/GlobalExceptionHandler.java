package com.example.lunalash.exception;

import com.example.lunalash.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletRequest request;

    // 處理資源找不到的例外 (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        // 從 Request 中取得開始時間，若無則以當前時間代替
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime == null) {
            startTime = System.currentTimeMillis();
        }

        // 建立統一格式的失敗回應
        ApiResponse<Object> response = new ApiResponse<>();
        response.setResultCode(404);
        response.setResultMsg(ex.getMessage());
        
        // 計算實際執行的耗時，不再寫死為 0ms
        long duration = System.currentTimeMillis() - startTime;
        response.setRunTime(duration + "ms");
        response.setResultData(null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 處理所有未預期的系統錯誤 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime == null) {
            startTime = System.currentTimeMillis();
        }

        ApiResponse<Object> response = new ApiResponse<>();
        response.setResultCode(500);
        response.setResultMsg("系統發生非預期錯誤：" + ex.getMessage());
        
        long duration = System.currentTimeMillis() - startTime;
        response.setRunTime(duration + "ms");
        response.setResultData(null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}