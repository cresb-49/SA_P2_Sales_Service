package com.sap.sales_service.sale.infrastructure.output.jpa.repository;

import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineSnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleLineSnackEntityRepository extends JpaRepository<SaleLineSnackEntity, UUID> {
    List<SaleLineSnackEntity> findAllBySaleId(UUID saleId);
}
