

package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaleCinemaAdapterTest {

    @Mock
    private CinemaGatewayPort cinemaGatewayPort;

    @InjectMocks
    private SaleCinemaAdapter adapter;

    @Test
    void existsById_shouldReturnTrue_whenGatewaySaysTrue() {
        // given
        UUID cinemaId = UUID.randomUUID();
        given(cinemaGatewayPort.existsById(cinemaId)).willReturn(true);

        // when
        boolean exists = adapter.existsById(cinemaId);

        // then
        assertThat(exists).isTrue();
        verify(cinemaGatewayPort).existsById(cinemaId);
    }

    @Test
    void existsById_shouldReturnFalse_whenGatewaySaysFalse() {
        // given
        UUID cinemaId = UUID.randomUUID();
        given(cinemaGatewayPort.existsById(cinemaId)).willReturn(false);

        // when
        boolean exists = adapter.existsById(cinemaId);

        // then
        assertThat(exists).isFalse();
        verify(cinemaGatewayPort).existsById(cinemaId);
    }
}