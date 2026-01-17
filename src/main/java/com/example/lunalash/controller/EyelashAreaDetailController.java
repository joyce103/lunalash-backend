package com.example.lunalash.controller;

import com.example.lunalash.dto.EyelashAreaDetailRequest;
import com.example.lunalash.dto.EyelashAreaDetailResponse;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.service.EyelashAreaDetailService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operation-items")
public class EyelashAreaDetailController {

	private final EyelashAreaDetailRepository repository;
    private final EyelashAreaDetailService eyelashAreaDetailService;

    public EyelashAreaDetailController(EyelashAreaDetailService eyelashAreaDetailService, EyelashAreaDetailRepository eyelashAreaDetailRepository) {
        this.repository = eyelashAreaDetailRepository;
    	this.eyelashAreaDetailService = eyelashAreaDetailService;
    }

    @PostMapping("/{operationItemId}/areas")
    public List<EyelashAreaDetailResponse> createAreas(
            @PathVariable Long operationItemId,
            @RequestBody List<EyelashAreaDetailEntity> request
    ) {
        List<EyelashAreaDetailEntity> saved =
                eyelashAreaDetailService.createAreas(operationItemId, request);

        return saved.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EyelashAreaDetailResponse toResponse(EyelashAreaDetailEntity entity) {
    	EyelashAreaDetailResponse res = new EyelashAreaDetailResponse();
        res.setEyelashAreaDetailId(entity.getEyelashAreaDetailId());
        res.setPosition(entity.getPosition());
        res.setLashCount(entity.getLashCount());
        res.setLashLengths(entity.getLashLengths());
        res.setLashCurls(entity.getLashCurls());
        return res;
    }
    
    // 刪除單一睫毛區域資料
    @PostMapping("/area")    
    public ResponseEntity<Void> delete(@RequestBody EyelashAreaDetailRequest request) {
    	System.out.println("Delete API called: eyelashAreaDetailId=" + request.getEyelashAreaDetailId() +
                ", operationItemId=" + request.getOperationItemId());
    	Long eyelashAreaDetailId = request.getEyelashAreaDetailId();
        Long operationItemId = request.getOperationItemId();
        try {
            eyelashAreaDetailService.deleteOneArea(eyelashAreaDetailId, operationItemId);
            return ResponseEntity.noContent().build(); // 204
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 刪除所有睫毛區域資料
    @DeleteMapping("/{operationItemId}/areas")    
    public ResponseEntity<Void> deleteAreas(
            @PathVariable Long operationItemId
    ) {
    	eyelashAreaDetailService.deleteAreasByOperationItemId(operationItemId);
        return ResponseEntity.noContent().build();
    }
}
