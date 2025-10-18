package com.sap.sales_service.snacks.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@Getter
public class Snack {

    private final UUID id;
    private final UUID cinemaId;
    private String name;
    private BigDecimal price;
    private boolean externalImage;
    private String imageUrl;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Snack(UUID cinemaId, String name, BigDecimal price, boolean externalImage, String imageUrl) {
        this.id = UUID.randomUUID();
        this.cinemaId = cinemaId;
        this.name = name;
        this.price = price;
        this.externalImage = externalImage;
        this.imageUrl = imageUrl;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String name, BigDecimal price, boolean externalImage, String imageUrl) {
        var changed = false;
        if (name != null && !name.isEmpty()) {
            this.name = name;
            changed = true;
        }
        if (price != null) {
            this.price = price;
            changed = true;
        }
        this.externalImage = externalImage;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
            changed = true;
        }
        if (changed) this.updatedAt = LocalDateTime.now();
    }

    public void toggleActive() {
        this.active = !this.active;
        this.updatedAt = LocalDateTime.now();
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
