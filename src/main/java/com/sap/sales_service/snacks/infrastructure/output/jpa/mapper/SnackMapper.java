package com.sap.sales_service.snacks.infrastructure.output.jpa.mapper;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.infrastructure.output.jpa.entity.SnackEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SnackMapper {

    public Snack toDomain(SnackEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Snack(
                entity.getId(),
                entity.getCinemaId(),
                entity.getName(),
                entity.getPrice(),
                entity.isExternalImage(),
                entity.getImageUrl(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public SnackEntity toEntity(Snack snack) {
        if (snack == null) {
            return null;
        }
        return new SnackEntity(
                snack.getId(),
                snack.getCinemaId(),
                snack.getName(),
                snack.getPrice(),
                snack.isExternalImage(),
                snack.getImageUrl(),
                snack.isActive(),
                snack.getCreatedAt(),
                snack.getUpdatedAt()
        );
    }
}
