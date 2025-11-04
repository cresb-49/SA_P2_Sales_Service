package com.sap.sales_service.sale.infrastructure.output.jpa.repository;

import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.TicketSalesByFunctionView;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleLineTicketEntityRepository extends JpaRepository<SaleLineTicketEntity, UUID> {
    List<SaleLineTicketEntity> findAllBySaleId(UUID saleId);

    @Query(value = """
            SELECT
                t.cinema_function_id AS functionId,
                t.cinema_id          AS cinemaId,
                t.cinema_room_id     AS cinemaRoomId,
                t.movie_id           AS movieId,
                COUNT(t.id)          AS ticketsSold
            FROM tickets t
            JOIN sale_line_tickets slt ON slt.id = t.sale_line_ticket_id
            JOIN sales s               ON s.id = slt.sale_id
            WHERE s.paid_at IS NOT NULL
              AND s.paid_at >= :from
              AND s.paid_at <  :to
              AND slt.status = 'PURCHASED'
            GROUP BY t.cinema_function_id, t.cinema_id, t.cinema_room_id, t.movie_id
            ORDER BY ticketsSold DESC
            """, nativeQuery = true)
    List<TicketSalesByFunctionView> findTicketsSoldByFunction(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
