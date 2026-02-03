package com.example.lunalash.controller;

import com.example.lunalash.dto.EyelashAreaDetailRequest;
import com.example.lunalash.dto.EyelashAreaDetailResponse;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.service.EyelashAreaDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationArea")
@Tag(name = "操作區域資料")
public class EyelashAreaDetailController {

    private final EyelashAreaDetailService eyelashAreaDetailService;

    public EyelashAreaDetailController(EyelashAreaDetailService eyelashAreaDetailService) {
        this.eyelashAreaDetailService = eyelashAreaDetailService;
    }
    
    @Operation(summary = "批次新增睫毛區域資料")
    @PostMapping("/createAreas")
    public List<EyelashAreaDetailResponse> createAreas(@RequestBody EyelashAreaDetailRequest request) {
        return eyelashAreaDetailService.createAreas(request);
    }
    
    @Operation(summary = "刪除睫毛區域資料(單筆)")
    @PostMapping("/deleteArea")
    public void delete(@RequestBody EyelashAreaDetailRequest request) {
        eyelashAreaDetailService.deleteOneArea(
            request.getEyelashAreaDetailId(), 
            request.getOperationItemId()
        );
    }
    
    @Operation(summary = "刪除睫毛區域資料(多筆)")
    @PostMapping("/deleteAreas")
    public void deleteAreas(@RequestBody EyelashAreaDetailRequest request) {
        eyelashAreaDetailService.deleteAreasByOperationItemId(request.getOperationItemId());
    }
}