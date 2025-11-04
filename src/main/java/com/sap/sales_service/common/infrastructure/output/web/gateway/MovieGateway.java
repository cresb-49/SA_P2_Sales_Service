package com.sap.sales_service.common.infrastructure.output.web.gateway;

import com.sap.common_lib.dto.response.movie.MovieResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.port.MovieGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@AllArgsConstructor
public class MovieGateway implements MovieGatewayPort {

    private final WebClient.Builder webClient;
    private static final String MOVIE_SERVICE_URL = "http://gateway/api/v1/movies";

    @Override
    public MovieResponseDTO getMovieById(UUID movieId) {
        return webClient.build()
                .get()
                .uri(MOVIE_SERVICE_URL + "/public/" + movieId)
                .retrieve()
                .bodyToMono(MovieResponseDTO.class)
                .block();
    }
}
