package com.sap.sales_service.snacks.infrastructure.input.web.mappers;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.infrastructure.input.web.dtos.SnackResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SnackResponseMapper {

    public SnackResponseDTO toResponse(Snack snack) {
        if (snack == null) {
            return null;
        }
        return new SnackResponseDTO(
                snack.getId(),
                snack.getName(),
                snack.getPrice(),
                snack.getImageUrl(),
                snack.getCreatedAt(),
                snack.getUpdatedAt()
        );
    }

    public Page<SnackResponseDTO> toPageResponse(Page<Snack> snacks) {
        return snacks.map(this::toResponse);
    }

    public List<SnackResponseDTO> toListResponse(List<Snack> snacks) {
        return snacks.stream().map(this::toResponse).toList();
    }
}
