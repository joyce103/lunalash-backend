package com.example.lunalash.repository;

import com.example.lunalash.entity.EyelashAreaDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EyelashAreaDetailRepository extends JpaRepository<EyelashAreaDetailEntity, Long> {

    List<EyelashAreaDetailEntity> findByOperationItemId(Long operationItemId);

    boolean existsByOperationItemId_OperationItemId(Long operationItemId);
    int deleteByEyelashAreaDetailIdAndOperationItem_OperationItemId(Long eyelashAreaDetailId, Long operationItemId);
    void deleteByOperationItem_OperationItemId(Long operationItemId);
}
