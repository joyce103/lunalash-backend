package com.example.lunalash.service;

import com.example.lunalash.entity.EyelashAreaDetailEntity;
import com.example.lunalash.entity.OperationItemEntity;
import com.example.lunalash.repository.EyelashAreaDetailRepository;
import com.example.lunalash.repository.OperationItemRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<EyelashAreaDetailEntity> createAreas(Long operationItemId, List<EyelashAreaDetailEntity> areas) {
        OperationItemEntity op = operationRepo.findById(operationItemId)
                .orElseThrow(() -> new RuntimeException("操作項目不存在"));

        for (EyelashAreaDetailEntity area : areas) {
            area.setOperationItem(op);
        }

        return areaRepo.saveAll(areas);
    }

    public List<EyelashAreaDetailEntity> getByOperationItemId(Long operationItemId) {
        return areaRepo.findByOperationItemId(operationItemId);
    }
    
    @Transactional
    public void deleteOneArea(Long eyelashAreaDetailId, Long operationItemId) {
        int deleted = areaRepo.deleteByEyelashAreaDetailIdAndOperationItem_OperationItemId(
        		eyelashAreaDetailId, operationItemId
               );
        if (deleted == 0) {
            throw new EntityNotFoundException(
                    "Area not found or not belong to this operationItem"
            );
        }
    }


    @Transactional
    public void deleteAreasByOperationItemId(Long operationItemId) {
    	areaRepo.deleteByOperationItem_OperationItemId(operationItemId);
    }
}
