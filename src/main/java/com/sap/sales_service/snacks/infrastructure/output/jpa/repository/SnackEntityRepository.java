package com.sap.sales_service.snacks.infrastructure.output.jpa.repository;

import com.sap.sales_service.snacks.infrastructure.output.jpa.entity.SnackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SnackEntityRepository extends JpaRepository<SnackEntity, UUID> {
    Page<SnackEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<SnackEntity> findByIdIn(List<UUID> ids);
}
