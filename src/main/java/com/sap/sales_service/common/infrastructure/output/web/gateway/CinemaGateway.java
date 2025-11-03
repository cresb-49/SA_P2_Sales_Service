package com.sap.sales_service.common.infrastructure.output.web.gateway;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CinemaGateway implements CinemaGatewayPort {

    private final WebClient.Builder webClient;
    private static final String SHOWTIME_SERVICE_URL = "http://gateway/api/v1/showtimes";
    private static final String CINEMA_SERVICE_URL = "http://gateway/api/v1/cinemas";

    @Override
    public boolean existsById(UUID cinemaId) {
        return Boolean.TRUE.equals(webClient.build()
                .get()
                .uri(CINEMA_SERVICE_URL + "/public/" + cinemaId)
                .retrieve()
                .bodyToMono(CinemaResponseDTO.class)
                .map(cinema -> true)
                .defaultIfEmpty(false)
                .block());
    }

    @Override
    public CinemaResponseDTO findCinemaById(UUID cinemaId) {
        return webClient.build()
                .get()
                .uri(CINEMA_SERVICE_URL + "/public/" + cinemaId)
                .retrieve()
                .bodyToMono(CinemaResponseDTO.class)
                .block();
    }

    @Override
    public ShowtimeResponseDTO findFunctionById(UUID functionId) {
        return webClient.build()
                .get()
                .uri(SHOWTIME_SERVICE_URL + "/public/" + functionId)
                .retrieve()
                .bodyToMono(ShowtimeResponseDTO.class)
                .block();
    }

    @Override
    public List<ShowtimeResponseDTO> findFunctionsByIds(List<UUID> functionIds) {
        return webClient.build()
                .post()
                .uri(SHOWTIME_SERVICE_URL + "/public/ids")
                .bodyValue(functionIds)
                .retrieve()
                .bodyToFlux(ShowtimeResponseDTO.class)
                .collectList()
                .block();
    }
}
