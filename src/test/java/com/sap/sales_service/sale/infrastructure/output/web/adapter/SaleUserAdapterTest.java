

package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.UserGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleUserAdapterTest {

    @Mock
    private UserGatewayPort userGatewayPort;

    @InjectMocks
    private SaleUserAdapter adapter;

    @Test
    void existsById_shouldReturnTrue_whenGatewayReturnsTrue() {
        UUID userId = UUID.randomUUID();
        when(userGatewayPort.existsById(userId)).thenReturn(true);

        boolean result = adapter.existsById(userId);

        assertTrue(result);
        verify(userGatewayPort).existsById(userId);
    }

    @Test
    void existsById_shouldReturnFalse_whenGatewayReturnsFalse() {
        UUID userId = UUID.randomUUID();
        when(userGatewayPort.existsById(userId)).thenReturn(false);

        boolean result = adapter.existsById(userId);

        assertFalse(result);
        verify(userGatewayPort).existsById(userId);
    }
}