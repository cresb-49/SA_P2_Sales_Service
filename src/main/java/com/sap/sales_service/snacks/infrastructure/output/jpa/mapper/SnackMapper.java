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
                entity.getName(),
                entity.getPrice(),
                entity.getImageUrl(),
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
                snack.getName(),
                snack.getPrice(),
                snack.getImageUrl(),
                snack.getCreatedAt(),
                snack.getUpdatedAt()
        );
    }
}
