package com.sap.sales_service.snacks.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
public class Snack {
    private final UUID id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(toBuilder = true)
    public Snack(UUID id, String name, BigDecimal price, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public void update(String name, BigDecimal price, String imageUrl) {
        var changed = false;
        if (name != null && !name.isEmpty()) {
            this.name = name;
            changed = true;
        }
        if (price != null) {
            this.price = price;
            changed = true;
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
            changed = true;
        }
        if (changed) this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (this.name == null || this.name.isEmpty()) {
            throw new IllegalArgumentException("Snack name cannot be null or empty");
        }
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Snack price cannot be null or negative");
        }
        if (this.imageUrl == null || this.imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Snack imageUrl cannot be null or empty");
        }
    }
}
