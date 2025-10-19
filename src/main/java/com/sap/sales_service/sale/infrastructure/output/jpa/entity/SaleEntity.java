package com.sap.sales_service.sale.infrastructure.output.jpa.entity;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sales")
public class SaleEntity {
    @Id
    private UUID id;
    @Column(nullable = true)
    private UUID clientId;
    @Column(nullable = false)
    private UUID cinemaId;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SaleStatusType status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = true)
    private LocalDateTime paidAt;
}
