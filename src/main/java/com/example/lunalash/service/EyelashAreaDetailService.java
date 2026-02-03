package com.example.lunalash.service;

import com.example.lunalash.dto.EyelashAreaDetailRequest;
import com.example.lunalash.dto.EyelashAreaDetailResponse;
import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.repository.OperationItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EyelashAreaDetailService {

    private final EyelashAreaDetailRepository areaRepo;
    private final OperationItemRepository operationRepo;

    public EyelashAreaDetailService(EyelashAreaDetailRepository areaRepo,
                                    OperationItemRepository operationRepo) {
        this.areaRepo = areaRepo;
        this.operationRepo = operationRepo;
    }

    @Transactional
    public List<EyelashAreaDetailResponse> createAreas(EyelashAreaDetailRequest batchRequest) {
        OperationItemEntity op = operationRepo.findById(batchRequest.getOperationItemId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到操作項目: " + batchRequest.getOperationItemId()));

        List<EyelashAreaDetailEntity> entities = batchRequest.getAreas().stream().map(req -> {
            EyelashAreaDetailEntity entity = new EyelashAreaDetailEntity();
            entity.setPosition(req.getPosition());
            entity.setLashCount(req.getLashCount());
            entity.setLashLengths(req.getLashLengths());
            entity.setLashCurls(req.getLashCurls());
            entity.setOperationItem(op);
            return entity;
        }).toList();

        List<EyelashAreaDetailEntity> savedEntities = areaRepo.saveAll(entities);

        return savedEntities.stream().map(this::toResponse).toList();
    }
    
    private EyelashAreaDetailResponse toResponse(EyelashAreaDetailEntity entity) {
        EyelashAreaDetailResponse resp = new EyelashAreaDetailResponse();
        resp.setEyelashAreaDetailId(entity.getEyelashAreaDetailId());
        resp.setPosition(entity.getPosition());
        resp.setLashCount(entity.getLashCount());
        resp.setLashLengths(entity.getLashLengths());
        resp.setLashCurls(entity.getLashCurls());
        return resp;
    }

    @Transactional
    public void deleteOneArea(Long eyelashAreaDetailId, Long operationItemId) {
        int deleted = areaRepo.deleteByEyelashAreaDetailIdAndOperationItem_OperationItemId(
                eyelashAreaDetailId, operationItemId
        );
        if (deleted == 0) {
            throw new ResourceNotFoundException("找不到指定的區域資料或該資料不屬於此操作項目");
        }
    }

    @Transactional
    public void deleteAreasByOperationItemId(Long operationItemId) {
        if (!areaRepo.existsByOperationItem_OperationItemId(operationItemId)) {
            throw new ResourceNotFoundException("該操作項目 ID: " + operationItemId + " 下無區域資料可刪除");
        }
        areaRepo.deleteByOperationItem_OperationItemId(operationItemId);
    }
}