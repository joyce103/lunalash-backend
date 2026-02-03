package com.example.lunalash.repository;

import com.example.lunalash.entity.EyelashAreaDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EyelashAreaDetailRepository extends JpaRepository<EyelashAreaDetailEntity, Long> {

    // 依據操作項目 ID 查詢
    List<EyelashAreaDetailEntity> findByOperationItem_OperationItemId(Long operationItemId);

    // 檢查該操作項目是否存在明細
    boolean existsByOperationItem_OperationItemId(Long operationItemId);

    // 刪除該操作項目下所有明細
    void deleteByOperationItem_OperationItemId(Long operationItemId);

    // 刪除特定明細且屬於特定操作項目
    int deleteByEyelashAreaDetailIdAndOperationItem_OperationItemId(Long eyelashAreaDetailId, Long operationItemId);
}