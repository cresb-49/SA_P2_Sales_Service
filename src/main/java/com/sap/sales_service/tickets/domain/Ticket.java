package com.sap.sales_service.tickets.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Ticket {
    private final UUID id;
    private final UUID saleLineTicketId;
    private final UUID cinemaFunctionId;
    private final UUID cinemaId;
    private final UUID cinemaRoomId;
    private final UUID movieId;
    private boolean used;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(
            UUID saleLineTicketId, UUID cinemaFunctionId, UUID cinemaId,
            UUID cinemaRoomId, UUID movieId
    ) {
        this.id = UUID.randomUUID();
        this.saleLineTicketId = saleLineTicketId;
        this.cinemaFunctionId = cinemaFunctionId;
        this.cinemaId = cinemaId;
        this.cinemaRoomId = cinemaRoomId;
        this.movieId = movieId;
        this.used = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsUsed() {
        if (this.used) {
            throw new RuntimeException("Ticket is already used");
        }
        this.used = true;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
