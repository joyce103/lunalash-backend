package com.example.lunalash.repository;

import com.example.lunalash.entity.ServiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItemEntity, Long> {

    // 訪客預約表單用：只給啟用中的服務，依排序顯示
    List<ServiceItemEntity> findByIsActiveTrueOrderBySortOrderAsc();

    // 後台管理用：全部服務都要看到（含停用）
    List<ServiceItemEntity> findAllByOrderBySortOrderAsc();
}
