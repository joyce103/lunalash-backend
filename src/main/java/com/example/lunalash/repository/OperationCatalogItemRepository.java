package com.example.lunalash.repository;

import com.example.lunalash.entity.OperationCatalogItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationCatalogItemRepository extends JpaRepository<OperationCatalogItemEntity, Long> {

    // 訪客預約表單用：只給啟用中的項目，依排序顯示
    List<OperationCatalogItemEntity> findByIsActiveTrueOrderBySortOrderAsc();

    // 後台管理用：全部項目都要看到（含停用）
    List<OperationCatalogItemEntity> findAllByOrderBySortOrderAsc();
}
