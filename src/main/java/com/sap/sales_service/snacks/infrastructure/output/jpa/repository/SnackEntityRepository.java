package com.sap.sales_service.snacks.infrastructure.output.jpa.repository;

import com.sap.sales_service.snacks.infrastructure.output.jpa.entity.SnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SnackEntityRepository extends JpaRepository<SnackEntity, UUID>, JpaSpecificationExecutor<SnackEntity> {
    Optional<SnackEntity> findByNameIgnoreCaseAndCinemaId(String name, UUID cinemaId);
}
