package com.sap.sales_service.sale.infrastructure.output.web.mapper;

import com.sap.common_lib.dto.response.movie.MovieResponseDTO;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MovieSummaryViewMapper {

    public MovieSummaryView toView(MovieResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new MovieSummaryView(
                dto.id(),
                dto.title()
        );
    }

    public List<MovieSummaryView> toViewList(List<MovieResponseDTO> dtos) {
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(this::toView)
                .toList();
    }
}
