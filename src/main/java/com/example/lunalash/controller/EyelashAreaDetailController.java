package com.example.lunalash.controller;

import com.example.lunalash.dto.EyelashAreaDetailRequest;
import com.example.lunalash.service.EyelashAreaDetailService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationItems")
public class EyelashAreaDetailController {

    private final EyelashAreaDetailService eyelashAreaDetailService;

    public EyelashAreaDetailController(EyelashAreaDetailService eyelashAreaDetailService) {
    	this.eyelashAreaDetailService = eyelashAreaDetailService;
    }
    
    // 刪除單一睫毛區域資料
    @PostMapping("/area")    
    public ResponseEntity<Void> delete(@RequestBody EyelashAreaDetailRequest request) {
    	Long eyelashAreaDetailId = request.eyelashAreaDetailId;
        Long operationItemId = request.operationItemId;
        try {
            eyelashAreaDetailService.deleteOneArea(eyelashAreaDetailId, operationItemId);
            return ResponseEntity.noContent().build(); // 204
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 刪除所有睫毛區域資料
    @DeleteMapping("/{operationItemId}/areas")    
    public ResponseEntity<Void> deleteAreas(@PathVariable Long operationItemId) {
    	eyelashAreaDetailService.deleteAreasByOperationItemId(operationItemId);
        return ResponseEntity.noContent().build();
    }
}
