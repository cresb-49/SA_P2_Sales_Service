package com.sap.sales_service.snacks.infrastructure.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "snacks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SnackEntity {
    @Id
    private  UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    private  LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
