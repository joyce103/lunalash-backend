package com.example.lunalash.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // 透過建構子將 application.properties 裡的金鑰注入，並初始化 Cloudinary
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // 上傳圖片並回傳網址
    public String uploadImage(MultipartFile file) {
        try {
            // 將收到的 MultipartFile 上傳到 Cloudinary，並指定放在 "lumelash" 這個資料夾下
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                    ObjectUtils.asMap("folder", "lumelash"));
            
            // 拿回安全的 HTTPS 圖片網址
            return uploadResult.get("secure_url").toString();
            
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗：" + e.getMessage());
        }
    }
}