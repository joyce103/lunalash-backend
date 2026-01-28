package com.example.lunalash.controller;

import com.example.lunalash.dto.EyelashAreaDetailRequest;
import com.example.lunalash.service.EyelashAreaDetailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationItems")
@Tag(name = "操作區域資料")
public class EyelashAreaDetailController {

    private final EyelashAreaDetailService eyelashAreaDetailService;

    public EyelashAreaDetailController(EyelashAreaDetailService eyelashAreaDetailService) {
    	this.eyelashAreaDetailService = eyelashAreaDetailService;
    }
    
    @Operation(summary = "刪除睫毛區域資料(單筆)")
    @PostMapping("/area")    
    public ResponseEntity<Void> delete(@RequestBody EyelashAreaDetailRequest request) {
    	Long eyelashAreaDetailId = request.getEyelashAreaDetailId();
        Long operationItemId = request.getOperationItemId();
        try {
            eyelashAreaDetailService.deleteOneArea(eyelashAreaDetailId, operationItemId);
            return ResponseEntity.noContent().build(); // 204
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "刪除睫毛區域資料(多筆)")
    @DeleteMapping("/{operationItemId}/areas")    
    public ResponseEntity<Void> deleteAreas(@Parameter(description="操作項目編號")@PathVariable Long operationItemId) {
    	eyelashAreaDetailService.deleteAreasByOperationItemId(operationItemId);
        return ResponseEntity.noContent().build();
    }
}
