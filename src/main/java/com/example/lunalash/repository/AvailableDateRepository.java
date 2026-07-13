package com.example.lunalash.repository;

import com.example.lunalash.entity.AvailableDateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableDateRepository extends JpaRepository<AvailableDateEntity, Long> {

    Optional<AvailableDateEntity> findByAppointmentDate(LocalDate appointmentDate);

    List<AvailableDateEntity> findAllByOrderByAppointmentDateAsc();

    List<AvailableDateEntity> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);
}
