package com.sap.sales_service.common.infrastructure.output.web.gateway;

import com.sap.common_lib.dto.response.movie.MovieResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieGatewayTest {

    private static final UUID MOVIE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_MOVIE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MovieGateway movieGateway;

    @BeforeEach
    void setup() {
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void getMovieById_shouldReturnMovie() {
        MovieResponseDTO movie = mock(MovieResponseDTO.class);
        mockGetChain(Mono.just(movie));

        var result = movieGateway.getMovieById(MOVIE_ID);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    void getMoviesByIds_shouldReturnMovieList() {
        MovieResponseDTO movie1 = mock(MovieResponseDTO.class);
        MovieResponseDTO movie2 = mock(MovieResponseDTO.class);
        mockPostChain(Flux.fromIterable(List.of(movie1, movie2)));

        var result = movieGateway.getMoviesByIds(List.of(MOVIE_ID, OTHER_MOVIE_ID));

        assertThat(result).containsExactly(movie1, movie2);
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

    private void mockPostChain(Flux<MovieResponseDTO> flux) {
        @SuppressWarnings("rawtypes")
        WebClient.RequestBodyUriSpec bodySpec = mock(WebClient.RequestBodyUriSpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(webClient.post()).thenReturn(bodySpec);
        when(bodySpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MovieResponseDTO.class)).thenReturn(flux);
    }
}
