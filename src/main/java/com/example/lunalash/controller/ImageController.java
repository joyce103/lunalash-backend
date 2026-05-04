package com.example.lunalash.controller;

import com.example.lunalash.service.CloudinaryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final CloudinaryService cloudinaryService;

    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload-multiple")
    public Map<String, List<String>> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("請至少選擇一張圖片");
        }

        List<String> uploadedUrls = new ArrayList<>();

        // 跑迴圈，把每一張圖片都丟給 Cloudinary
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                uploadedUrls.add(imageUrl);
            }
        }

        // 回傳一個包含所有圖片網址的 List
        Map<String, List<String>> responseData = new HashMap<>();
        responseData.put("imageUrls", uploadedUrls);
        
        return responseData;
    }
}