package com.sap.sales_service.tickets.infrastructure.output.jpa.repository;

import com.sap.sales_service.tickets.infrastructure.output.jpa.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketEntityRepository extends JpaRepository<TicketEntity, UUID>, JpaSpecificationExecutor<TicketEntity> {
    Optional<TicketEntity> findBySaleLineTicketId(UUID saleLineTicketId);
    List<TicketEntity> findBySaleLineTicketIdIn(List<UUID> saleLineTicketIds);
    Optional<TicketEntity> findByCinemaFunctionIdAndSeatId(UUID cinemaFunctionId, UUID seatId);
}
