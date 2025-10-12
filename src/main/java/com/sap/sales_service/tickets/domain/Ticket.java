package com.sap.sales_service.tickets.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Ticket {
    private UUID id;
    private UUID cinemaFunctionId;
    private UUID cinemaId;
    private UUID cinemaRoomId;
    private UUID seatId;
    private UUID userId;
    private BigDecimal cinemaFunctionPrice;
    private TicketStatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(UUID cinemaFunctionId, UUID cinemaId, UUID cinemaRoomId, UUID seatId, UUID userId, BigDecimal cinemaFunctionPrice) {
        this.id = UUID.randomUUID();
        this.cinemaFunctionId = cinemaFunctionId;
        this.cinemaId = cinemaId;
        this.cinemaRoomId = cinemaRoomId;
        this.seatId = seatId;
        this.userId = userId;
        this.cinemaFunctionPrice = cinemaFunctionPrice;
        this.status = TicketStatusType.RESERVED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (this.cinemaFunctionId == null) {
            throw new IllegalArgumentException("Cinema function ID cannot be null");
        }
        if (this.cinemaId == null) {
            throw new IllegalArgumentException("Cinema ID cannot be null");
        }
        if (this.cinemaRoomId == null) {
            throw new IllegalArgumentException("Cinema room ID cannot be null");
        }
        if (this.seatId == null) {
            throw new IllegalArgumentException("Seat ID cannot be null");
        }
        if (this.userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (this.cinemaFunctionPrice == null || this.cinemaFunctionPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cinema function price cannot be null or negative");
        }
    }

    public void cancel() {
        if (this.status != TicketStatusType.RESERVED) {
            throw new RuntimeException("Only reserved tickets can be cancelled");
        }
        this.status = TicketStatusType.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void purchase() {
        if (this.status != TicketStatusType.RESERVED) {
            throw new RuntimeException("Only reserved tickets can be purchased");
        }
        this.status = TicketStatusType.PURCHASED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reserve() {
        if (this.status != TicketStatusType.PENDING) {
            throw new RuntimeException("Only cancelled tickets can be reserved again");
        }
        this.status = TicketStatusType.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void pend() {
        if (this.status != TicketStatusType.RESERVED) {
            throw new RuntimeException("Only reserved tickets can be set to pending");
        }
        this.status = TicketStatusType.PENDING;
        this.updatedAt = LocalDateTime.now();
    }


}
