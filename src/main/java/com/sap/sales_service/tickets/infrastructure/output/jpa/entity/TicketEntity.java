package com.sap.sales_service.tickets.infrastructure.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID saleLineTicketId;
    @Column(nullable = false)
    private UUID cinemaFunctionId;
    @Column(nullable = false)
    private UUID cinemaId;
    @Column(nullable = false)
    private UUID cinemaRoomId;
    @Column(nullable = false)
    private UUID seatId;
    @Column(nullable = false)
    private UUID movieId;
    @Column(nullable = false)
    private boolean used;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
