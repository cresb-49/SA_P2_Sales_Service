package com.sap.sales_service.tickets.infrastructure.output.web.mapper;

import com.sap.common_lib.dto.response.movie.MovieResponseDTO;
import com.sap.sales_service.tickets.domain.dtos.MovieView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TicketMovieViewMapper {

    public MovieView toDomain(MovieResponseDTO movieResponseDTO) {
        if (movieResponseDTO == null) {
            return null;
        }
        return new MovieView(
                movieResponseDTO.title()
        );
    }

    public List<MovieView> toDomainList(List<MovieResponseDTO> movieResponseDTOs) {
        return movieResponseDTOs.stream()
                .map(this::toDomain)
                .toList();
    }
}
