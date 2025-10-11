package com.sap.sales_service.snacks.infrastructure.input.service.mappers;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.infrastructure.input.service.dtos.SnackInternalView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SnackInternalViewMapper {

    public SnackInternalView toView(Snack snack) {
        if (snack == null) {
            return null;
        }
        return new SnackInternalView(
                snack.getId(),
                snack.getName(),
                snack.getPrice(),
                snack.getImageUrl(),
                snack.getCreatedAt(),
                snack.getUpdatedAt()
        );
    }

    public List<SnackInternalView> toListView(List<Snack> snacks) {
        return snacks.stream().map(this::toView).toList();
    }
}
