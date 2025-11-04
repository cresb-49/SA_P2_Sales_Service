package com.sap.sales_service.sale.infrastructure.output.jpa.repository;

import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.CinemaSalesSummaryView;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleEntityRepository extends JpaRepository<SaleEntity, UUID>, JpaSpecificationExecutor<SaleEntity> {
    List<SaleEntity> findByClientId(UUID clientId);

    Page<SaleEntity> findByClientId(UUID clientId, Pageable pageable);

    @Query(value = """
            SELECT
                s.cinema_id    AS cinemaId,
                SUM(s.total_amount) AS totalAmount,
                COUNT(*)       AS totalSales
            FROM sales s
            WHERE s.paid_at IS NOT NULL
              AND s.paid_at >= :from
              AND s.paid_at <  :to
            GROUP BY s.cinema_id
            ORDER BY totalAmount DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<CinemaSalesSummaryView> findTopCinemaSales(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("limit") int limit
    );
}
