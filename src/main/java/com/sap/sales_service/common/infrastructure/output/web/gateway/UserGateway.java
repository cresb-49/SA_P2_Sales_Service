package com.sap.sales_service.common.infrastructure.output.web.gateway;

import com.sap.sales_service.common.infrastructure.output.web.port.UserGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UserGateway implements UserGatewayPort {

    private final WebClient.Builder webClient;
    private static final String USER_SERVICE_URL = "http://gateway/api/v1/users";

    @Override
    public boolean existsById(UUID userId) {
        boolean exists = Boolean.TRUE.equals(webClient.build()
                .head()
                .uri(USER_SERVICE_URL + "/{id}", userId)
                .exchangeToMono(resp -> Mono.just(resp.statusCode().is2xxSuccessful()))
                .onErrorReturn(WebClientResponseException.NotFound.class, false)
                .onErrorReturn(WebClientResponseException.class, false)
                .block());
        return exists;
    }
}
