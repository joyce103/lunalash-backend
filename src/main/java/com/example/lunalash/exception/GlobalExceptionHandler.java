package com.example.lunalash.exception;

import com.example.lunalash.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private HttpServletRequest request;

    private long getStartTime() {
        Long startTime = (Long) request.getAttribute("startTime");
        return startTime != null ? startTime : System.currentTimeMillis();
    }

    private ResponseEntity<ApiResponse<Object>> build(HttpStatus status, int resultCode, String msg) {
        ApiResponse<Object> response = ApiResponse.fail(resultCode, msg, getStartTime());
        return new ResponseEntity<>(response, status);
    }

    // 401：未登入 / 帳密錯誤 / Token 無效
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("未授權請求：{}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, 401, ex.getMessage());
    }

    // 403：已登入但權限不足
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("權限不足：{}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, 403, "權限不足，無法執行此操作");
    }

    // 400：@Valid 驗證失敗，彙整每個欄位的錯誤訊息
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + "：" + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("輸入驗證失敗：{}", msg);
        return build(HttpStatus.BAD_REQUEST, 400, msg.isEmpty() ? "輸入資料格式錯誤" : msg);
    }

    // 400：Bean Validation（例如 @Validated 在路徑/查詢參數上）驗證失敗
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("參數驗證失敗：{}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, 400, ex.getMessage());
    }

    // 400：Request Body 不是合法 JSON，或型別對不上
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("請求內容格式錯誤：{}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, 400, "請求內容格式錯誤，請確認送出的資料格式");
    }

    // 400：業務邏輯上的非法參數（例如缺少必要欄位、格式不符）
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("非法參數：{}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, 400, ex.getMessage());
    }

    // 409：核准預約時，同一時段已經被別的預約搶先核准 (race condition)
    @ExceptionHandler(SlotConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleSlotConflict(SlotConflictException ex) {
        log.warn("時段衝突：{}", ex.getMessage());
        return build(HttpStatus.CONFLICT, 409, ex.getMessage());
    }

    // 400：資料庫層級的約束衝突（例如刪除還被其他資料引用的服務項目）
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("資料庫約束衝突：{}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, 400, "此資料仍被其他紀錄使用，無法執行此操作");
    }

    // 404（以 resultCode 表達，HTTP 狀態維持 200）：
    // 前端既有頁面會直接檢查 resultCode 決定顯示「查無資料」，改成非 2xx 會讓這些頁面誤判為系統錯誤，故維持原行為
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.info("查無資源：{}", ex.getMessage());
        return build(HttpStatus.OK, 404, ex.getMessage());
    }

    // 404：路徑本身不存在
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn("API 路徑不存在：{}", ex.getRequestURL());
        return build(HttpStatus.NOT_FOUND, 404, "API 路徑不存在：" + ex.getRequestURL());
    }

    // 500：所有未預期的系統錯誤。詳細例外訊息只寫進伺服器 log，絕不回傳給前端，避免洩漏內部實作細節
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        log.error("系統發生非預期錯誤", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, 500, "系統發生錯誤，請稍後再試");
    }
}
