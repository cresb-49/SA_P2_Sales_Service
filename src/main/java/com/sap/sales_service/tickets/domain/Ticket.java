package com.sap.sales_service.tickets.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Ticket {
    private final UUID id;
    private final UUID saleLineTicketId;
    private final UUID cinemaFunctionId;
    private final UUID cinemaId;
    private final UUID cinemaRoomId;
    private final UUID seatId;
    private final UUID movieId;
    private boolean used;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(
            UUID saleLineTicketId, UUID cinemaFunctionId, UUID cinemaId, UUID cinemaRoomId, UUID seatId,
            UUID userId, UUID movieId, BigDecimal cinemaFunctionPrice
    ) {
        this.id = UUID.randomUUID();
        this.saleLineTicketId = saleLineTicketId;
        this.cinemaFunctionId = cinemaFunctionId;
        this.cinemaId = cinemaId;
        this.cinemaRoomId = cinemaRoomId;
        this.seatId = seatId;
        this.movieId = movieId;
        this.used = false;
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
        if (this.movieId == null) {
            throw new IllegalArgumentException("Movie ID cannot be null");
        }
    }

    public void markAsUsed() {
        if (this.used) {
            throw new RuntimeException("Ticket is already used");
        }
        this.used = true;
        this.updatedAt = LocalDateTime.now();
    }
}
