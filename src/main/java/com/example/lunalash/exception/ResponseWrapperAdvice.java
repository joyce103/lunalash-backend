package com.example.lunalash.exception;

import com.example.lunalash.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.example.lunalash.controller") // 只針對 Controller
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果 Controller 已經回傳 ApiResponse 型別，就不再重疊包裝
        return !returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, 
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        
        // 取得 Interceptor 存入的時間
        Long startTime = (Long) this.request.getAttribute("startTime");
        if (startTime == null) startTime = System.currentTimeMillis();

        // 直接調用你 ApiResponse.java 裡的 success 方法進行計算與包裝
        return ApiResponse.success(body, startTime);
    }
}