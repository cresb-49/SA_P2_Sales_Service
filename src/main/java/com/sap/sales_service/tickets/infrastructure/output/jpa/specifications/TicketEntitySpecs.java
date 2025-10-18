package com.sap.sales_service.tickets.infrastructure.output.jpa.specifications;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineTicketEntity;
import com.sap.sales_service.tickets.domain.TicketFilter;
import com.sap.sales_service.tickets.infrastructure.output.jpa.entity.TicketEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TicketEntitySpecs {
    public static Specification<TicketEntity> byFilter(TicketFilter f) {
        return Specification.allOf(
                eqUserId(f.userId()),
                eqSaleLineTicketId(f.saleLineTicketId()),
                eqCinemaFunctionId(f.cinemaFunctionId()),
                eqCinemaId(f.cinemaId()),
                eqCinemaRoomId(f.cinemaRoomId()),
                eqSeatId(f.seatId()),
                eqMovieId(f.movieId()),
                eqUsed(f.used()),
                leCreatedAt(f.maxCreatedAt()),
                geCreatedAt(f.minCreatedAt()),
                leUpdatedAt(f.maxUpdatedAt()),
                geUpdatedAt(f.minUpdatedAt()),
                eqSaleStatus(f.saleStatus()),
                eqTicketStatus(f.ticketStatus())
        );
    }

    public static Specification<TicketEntity> hasId(UUID id) {
        return (root, q, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    /**
     * Filtra tickets por el estado del ticket en la venta.
     * Camino lógico: Ticket.saleLineTicketId -> SaleLineTicket.saleId -> Sale.status
     */
    private static Specification<TicketEntity> eqSaleStatus(SaleStatusType saleStatus) {
        return (root, query, cb) -> {
            if (saleStatus == null) return null; // no aplicar filtro si viene null
            // Subconsulta que obtiene los IDs de SaleLineTicket cuyos Sale.status = saleStatus
            Subquery<UUID> saleLineIds = query.subquery(UUID.class);
            Root<SaleLineTicketEntity> sl = saleLineIds.from(SaleLineTicketEntity.class);
            Root<SaleEntity> s = saleLineIds.from(SaleEntity.class);
            saleLineIds.select(sl.get("id"))
                    .where(
                            cb.equal(sl.get("saleId"), s.get("id")),
                            cb.equal(s.get("status"), saleStatus)
                    );
            // Ticket.saleLineTicketId IN (subconsulta)
            return root.get("saleLineTicketId").in(saleLineIds);
        };
    }

    /**
     * Filtra tickets por el estado del ticket.
     * Camino lógico: Ticket.saleLineTicketId -> SaleLineTicket.status
     */
    private static Specification<TicketEntity> eqTicketStatus(TicketStatusType ticketStatus) {
        return (root, query, cb) -> {
            if (ticketStatus == null) return null; // no aplicar filtro si viene null
            // Subconsulta que obtiene los IDs de SaleLineTicket cuyos SaleLineTicket.status = ticketStatus
            Subquery<UUID> saleLineIds = query.subquery(UUID.class);
            Root<SaleLineTicketEntity> sl = saleLineIds.from(SaleLineTicketEntity.class);
            saleLineIds.select(sl.get("id"))
                    .where(
                            cb.equal(sl.get("status"), ticketStatus)
                    );
            // Ticket.saleLineTicketId IN (subconsulta)
            return root.get("saleLineTicketId").in(saleLineIds);
        };
    }

    /**
     * Filtra tickets por el userId dueño de la venta.
     * Camino lógico: Ticket.saleLineTicketId -> SaleLineTicket.saleId -> Sale.userId
     */
    public static Specification<TicketEntity> eqUserId(UUID userId) {
        return (root, query, cb) -> {
            if (userId == null) return null; // no aplicar filtro si viene null
            // Subconsulta que obtiene los IDs de SaleLineTicket cuyos Sale.userId = userId
            Subquery<UUID> saleLineIds = query.subquery(UUID.class);
            Root<SaleLineTicketEntity> sl = saleLineIds.from(SaleLineTicketEntity.class);
            Root<SaleEntity> s = saleLineIds.from(SaleEntity.class);
            saleLineIds.select(sl.get("id"))
                    .where(
                            cb.equal(sl.get("saleId"), s.get("id")),
                            cb.equal(s.get("userId"), userId)
                    );
            // Ticket.saleLineTicketId IN (subconsulta)
            return root.get("saleLineTicketId").in(saleLineIds);
        };
    }

    private static Specification<TicketEntity> eqSaleLineTicketId(java.util.UUID saleLineTicketId) {
        return (root, q, cb) -> saleLineTicketId == null ? null : cb.equal(root.get("saleLineTicketId"), saleLineTicketId);
    }

    private static Specification<TicketEntity> eqCinemaFunctionId(java.util.UUID cinemaFunctionId) {
        return (root, q, cb) -> cinemaFunctionId == null ? null : cb.equal(root.get("cinemaFunctionId"), cinemaFunctionId);
    }

    private static Specification<TicketEntity> eqCinemaId(java.util.UUID cinemaId) {
        return (root, q, cb) -> cinemaId == null ? null : cb.equal(root.get("cinemaId"), cinemaId);
    }

    private static Specification<TicketEntity> eqCinemaRoomId(java.util.UUID cinemaRoomId) {
        return (root, q, cb) -> cinemaRoomId == null ? null : cb.equal(root.get("cinemaRoomId"), cinemaRoomId);
    }

    private static Specification<TicketEntity> eqSeatId(java.util.UUID seatId) {
        return (root, q, cb) -> seatId == null ? null : cb.equal(root.get("seatId"), seatId);
    }

    private static Specification<TicketEntity> eqMovieId(java.util.UUID movieId) {
        return (root, q, cb) -> movieId == null ? null : cb.equal(root.get("movieId"), movieId);
    }

    private static Specification<TicketEntity> eqUsed(Boolean used) {
        return (root, q, cb) -> used == null ? null : cb.equal(root.get("used"), used);
    }

    private static Specification<TicketEntity> leCreatedAt(java.time.LocalDateTime maxCreatedAt) {
        return (root, q, cb) -> maxCreatedAt == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), maxCreatedAt);
    }

    private static Specification<TicketEntity> geCreatedAt(java.time.LocalDateTime minCreatedAt) {
        return (root, q, cb) -> minCreatedAt == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), minCreatedAt);
    }

    private static Specification<TicketEntity> leUpdatedAt(java.time.LocalDateTime maxUpdatedAt) {
        return (root, q, cb) -> maxUpdatedAt == null ? null : cb.lessThanOrEqualTo(root.get("updatedAt"), maxUpdatedAt);
    }

    private static Specification<TicketEntity> geUpdatedAt(java.time.LocalDateTime minUpdatedAt) {
        return (root, q, cb) -> minUpdatedAt == null ? null : cb.greaterThanOrEqualTo(root.get("updatedAt"), minUpdatedAt);
    }
}
