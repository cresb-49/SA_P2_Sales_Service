package com.sap.sales_service.sale.infrastructure.output.jpa.repository;

import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleLineTicketEntityRepository extends JpaRepository<SaleLineTicketEntity, UUID> {
}
