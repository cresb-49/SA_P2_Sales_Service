package com.sap.sales_service.sale.infrastructure.output.jpa.repository;

import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.SnackSalesByCinemaView;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineSnackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleLineSnackEntityRepository extends JpaRepository<SaleLineSnackEntity, UUID> {
    List<SaleLineSnackEntity> findAllBySaleId(UUID saleId);

    @Query(value = """
            SELECT
                s.cinema_id        AS cinemaId,
                sls.snack_id       AS snackId,
                SUM(sls.quantity)  AS totalQuantity,
                SUM(sls.total_price) AS totalAmount
            FROM sales s
            JOIN sale_line_snacks sls ON sls.sale_id = s.id
            WHERE s.paid_at >= :from
              AND s.paid_at <  :to
              AND (:cinemaId IS NULL OR s.cinema_id = :cinemaId)
            GROUP BY s.cinema_id, sls.snack_id
            ORDER BY s.cinema_id, totalAmount DESC
            """, nativeQuery = true)
    List<SnackSalesByCinemaView> findSnackSalesByCinema(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("cinemaId") UUID cinemaId
    );
}
