package com.example.lunalash.repository;

import com.example.lunalash.entity.AppointmentEntity;
import com.example.lunalash.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    boolean existsByAppointmentDateAndAppointmentTimeAndStatus(LocalDate appointmentDate, java.time.LocalTime appointmentTime, AppointmentStatus status);

    // 用來算某天已經被核准（真的占用）的時段
    List<AppointmentEntity> findByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);

    long countByAppointmentDate(LocalDate appointmentDate);

    long countByStatus(AppointmentStatus status);

    long countByAppointmentDateBetween(LocalDate start, LocalDate end);

    // 後台首頁月曆用：撈整個月的預約 (所有狀態)，依日期時間排序
    List<AppointmentEntity> findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(LocalDate start, LocalDate end);

    // 後台搜尋：姓名/電話模糊搜尋、日期、狀態皆為選填
    @Query("SELECT a FROM AppointmentEntity a WHERE " +
           "(:name IS NULL OR a.customerName LIKE CONCAT('%', :name, '%')) AND " +
           "(:phone IS NULL OR a.customerPhone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:date IS NULL OR a.appointmentDate = :date) AND " +
           "(:status IS NULL OR a.status = :status) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<AppointmentEntity> search(@Param("name") String name,
                                    @Param("phone") String phone,
                                    @Param("date") LocalDate date,
                                    @Param("status") AppointmentStatus status);
}
