package com.sap.sales_service.common.infrastructure.output.web.port;

import com.sap.common_lib.dto.response.movie.MovieResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MovieGatewayPort {
    MovieResponseDTO getMovieById(UUID movieId);
    List<MovieResponseDTO> getMoviesByIds(List<UUID> movieIds);
}
