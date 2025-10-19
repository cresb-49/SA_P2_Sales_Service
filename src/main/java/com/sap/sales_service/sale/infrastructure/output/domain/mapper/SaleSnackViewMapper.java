package com.sap.sales_service.sale.infrastructure.output.domain.mapper;

import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.snacks.infrastructure.input.service.dtos.SnackInternalView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SaleSnackViewMapper {

    public SnackView toView(SnackInternalView domainView) {
        if (domainView == null) {
            return null;
        }
        return new SnackView(
                domainView.id(),
                domainView.cinemaId(),
                domainView.name(),
                domainView.price(),
                domainView.imageUrl(),
                domainView.createdAt(),
                domainView.updatedAt()
        );
    }

    public List<SnackView> toViewList(List<SnackInternalView> domainViews) {
        if (domainViews == null) {
            return null;
        }
        return domainViews.stream()
                .map(this::toView)
                .toList();
    }
}
