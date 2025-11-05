package com.sap.sales_service.common.infrastructure.output.web.gateway;

import com.sap.sales_service.common.infrastructure.output.web.dto.CinemaIdsRequestDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaHallResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaMovieResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaGatewayTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_CINEMA_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FUNCTION_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CinemaGateway cinemaGateway;

    @BeforeEach
    void setup() {
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void existsById_shouldReturnTrue_whenCinemaPresent() {
        // Arrange
        var cinemaDto = sampleCinema(CINEMA_ID, "Cinema UX");
        mockGetChain(Mono.just(cinemaDto));

        // Act
        boolean exists = cinemaGateway.existsById(CINEMA_ID);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenCinemaMissing() {
        mockGetChain(Mono.empty());

        boolean exists = cinemaGateway.existsById(CINEMA_ID);

        assertThat(exists).isFalse();
    }

    @Test
    void findCinemaById_shouldReturnCinema() {
        var cinemaDto = sampleCinema(CINEMA_ID, "Cinema UX");
        mockGetChain(Mono.just(cinemaDto));

        var result = cinemaGateway.findCinemaById(CINEMA_ID);

        assertThat(result).isEqualTo(cinemaDto);
    }

    @Test
    void findCinemasByIds_shouldPostAndReturnList() {
        var cinemaList = List.of(
                sampleCinema(CINEMA_ID, "Cinema UX"),
                sampleCinema(OTHER_CINEMA_ID, "Cinema Center")
        );
        var bodySpec = mockPostChain(Flux.fromIterable(cinemaList), CinemaResponseDTO.class);

        var result = cinemaGateway.findCinemasByIds(List.of(CINEMA_ID, OTHER_CINEMA_ID));

        assertThat(result).containsExactlyElementsOf(cinemaList);
        ArgumentCaptor<CinemaIdsRequestDTO> bodyCaptor = ArgumentCaptor.forClass(CinemaIdsRequestDTO.class);
        verify(bodySpec).bodyValue(bodyCaptor.capture());
        assertThat(bodyCaptor.getValue().ids()).containsExactly(CINEMA_ID, OTHER_CINEMA_ID);
    }

    @Test
    void findFunctionById_shouldReturnShowtime() {
        var showtime = sampleShowtime(FUNCTION_ID);
        mockGetChain(Mono.just(showtime));

        var result = cinemaGateway.findFunctionById(FUNCTION_ID);

        assertThat(result).isEqualTo(showtime);
    }

    @Test
    void findFunctionsByIds_shouldReturnShowtimeList() {
        var showtimes = List.of(
                sampleShowtime(FUNCTION_ID),
                sampleShowtime(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"))
        );
        var bodySpec = mockPostChain(Flux.fromIterable(showtimes), ShowtimeResponseDTO.class);

        var result = cinemaGateway.findFunctionsByIds(List.of(FUNCTION_ID));

        assertThat(result).containsExactlyElementsOf(showtimes);
        verify(bodySpec).bodyValue(List.of(FUNCTION_ID));
    }

    private void mockGetChain(Mono<?> mono) {
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(mono);
    }

    private <T> WebClient.RequestBodyUriSpec mockPostChain(Flux<T> flux, Class<T> clazz) {
        @SuppressWarnings("rawtypes")
        WebClient.RequestBodyUriSpec bodySpec = mock(WebClient.RequestBodyUriSpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(webClient.post()).thenReturn(bodySpec);
        when(bodySpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(clazz)).thenReturn(flux);
        return bodySpec;
    }

    private CinemaResponseDTO sampleCinema(UUID id, String name) {
        return new CinemaResponseDTO(
                id,
                null,
                name,
                BigDecimal.TEN,
                LocalDate.of(2024, 1, 1)
        );
    }

    private ShowtimeResponseDTO sampleShowtime(UUID id) {
        var cinema = sampleCinema(CINEMA_ID, "Cinema UX");
        var hall = new CinemaHallResponseDTO(
                UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"),
                cinema,
                "Sala 1",
                10,
                10,
                true,
                true
        );
        var cinemaMovie = new CinemaMovieResponseDTO(
                UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"),
                cinema,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                true
        );
        return new ShowtimeResponseDTO(
                id,
                cinemaMovie,
                hall,
                LocalDateTime.of(2024, 1, 1, 18, 0),
                LocalDateTime.of(2024, 1, 1, 20, 0),
                100,
                BigDecimal.valueOf(8.5)
        );
    }
}
