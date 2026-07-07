package com.example.lunalash.repository;

import com.example.lunalash.entity.AdminAccountRequestEntity;
import com.example.lunalash.entity.AdminAccountRequestEntity.RequestStatus;
import com.example.lunalash.entity.AdminAccountRequestEntity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminAccountRequestRepository extends JpaRepository<AdminAccountRequestEntity, Long> {

    List<AdminAccountRequestEntity> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    boolean existsByStatusAndRequestTypeAndTargetAdminId(RequestStatus status, RequestType requestType, Long targetAdminId);

    boolean existsByStatusAndRequestTypeAndNewUsername(RequestStatus status, RequestType requestType, String newUsername);
}
