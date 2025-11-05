package com.sap.sales_service.common.infrastructure.output.web.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGatewayTest {

    private static final UUID USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void existsById_shouldReturnTrue_whenServiceResponds2xx() {
        mockHeadRequest(ClientResponse.create(HttpStatus.OK).build());

        boolean exists = userGateway.existsById(USER_ID);

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenServiceResponds404() {
        mockHeadRequest(ClientResponse.create(HttpStatus.NOT_FOUND).build());

        boolean exists = userGateway.existsById(USER_ID);

        assertThat(exists).isFalse();
    }

    private void mockHeadRequest(ClientResponse response) {
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(webClient.head()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        when(headersSpec.exchangeToMono(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Function<ClientResponse, Mono<Boolean>> function = invocation.getArgument(0);
            return function.apply(response);
        });
    }
}
