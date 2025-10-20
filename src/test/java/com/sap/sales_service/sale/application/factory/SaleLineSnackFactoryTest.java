package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSaleLineSnackPort;
import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleLineSnackFactoryTest {

    // Arrange constants
    private static final UUID SALE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SNACK_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SNACK_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock private FindSaleLineSnackPort findSaleLineSnackPort;
    @Mock private FindSnackPort findSnackPort;

    @InjectMocks private SaleLineSnackFactory factory;

    @Captor private ArgumentCaptor<List<UUID>> uuidListCaptor;

    private SaleLineSnack lineWithSnack(UUID snackId) {
        return new SaleLineSnack(SALE_ID, snackId, 2, BigDecimal.TEN);
    }

    private SnackView mockSnackView(UUID id) {
        SnackView view = mock(SnackView.class);
        when(view.id()).thenReturn(id);
        return view;
    }

    @BeforeEach
    void resetInteractions() {
        clearInvocations(findSaleLineSnackPort, findSnackPort);
    }

    @Test
    void saleLineSnackWithAllRelations_shouldAttachSnackViews_whenAllFound() {
        // Arrange
        var line1 = lineWithSnack(SNACK_ID_1);
        var line2 = lineWithSnack(SNACK_ID_2);
        when(findSaleLineSnackPort.findAllBySaleId(SALE_ID)).thenReturn(List.of(line1, line2));

        var view1 = mockSnackView(SNACK_ID_1);
        var view2 = mockSnackView(SNACK_ID_2);
        when(findSnackPort.findAllById(anyList())).thenReturn(List.of(view1, view2));

        // Act
        var result = factory.saleLineSnackWithAllRelations(SALE_ID);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSnackView()).isEqualTo(view1);
        assertThat(result.get(1).getSnackView()).isEqualTo(view2);

        verify(findSaleLineSnackPort).findAllBySaleId(SALE_ID);
        verify(findSnackPort).findAllById(uuidListCaptor.capture());
        assertThat(uuidListCaptor.getValue()).containsExactlyInAnyOrder(SNACK_ID_1, SNACK_ID_2);
        verifyNoMoreInteractions(findSaleLineSnackPort, findSnackPort);
    }

    @Test
    void saleLineSnackWithAllRelations_shouldLeaveNullSnackView_whenSomeMissing() {
        // Arrange
        var line1 = lineWithSnack(SNACK_ID_1);
        var line2 = lineWithSnack(SNACK_ID_2);
        when(findSaleLineSnackPort.findAllBySaleId(SALE_ID)).thenReturn(List.of(line1, line2));

        var view1 = mockSnackView(SNACK_ID_1);
        when(findSnackPort.findAllById(anyList())).thenReturn(List.of(view1)); // SNACK_ID_2 faltante

        // Act
        var result = factory.saleLineSnackWithAllRelations(SALE_ID);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSnackView()).isEqualTo(view1);
        assertThat(result.get(1).getSnackView()).isNull();

        verify(findSaleLineSnackPort).findAllBySaleId(SALE_ID);
        verify(findSnackPort).findAllById(anyList());
        verifyNoMoreInteractions(findSaleLineSnackPort, findSnackPort);
    }

    @Test
    void saleLineSnackWithAllRelations_single_shouldAttachSnackView_whenFound() {
        // Arrange
        var line = lineWithSnack(SNACK_ID_1);
        SnackView view = mock(SnackView.class);
        when(findSnackPort.findById(SNACK_ID_1)).thenReturn(Optional.of(view));

        // Act
        var result = factory.saleLineSnackWithAllRelations(line);

        // Assert
        assertThat(result.getSnackView()).isEqualTo(view);
        verify(findSnackPort).findById(SNACK_ID_1);
        verifyNoMoreInteractions(findSnackPort);
        verifyNoInteractions(findSaleLineSnackPort);
    }

    @Test
    void saleLineSnackWithAllRelations_single_shouldLeaveNull_whenNotFound() {
        // Arrange
        var line = lineWithSnack(SNACK_ID_2);
        when(findSnackPort.findById(SNACK_ID_2)).thenReturn(Optional.empty());

        // Act
        var result = factory.saleLineSnackWithAllRelations(line);

        // Assert
        assertThat(result.getSnackView()).isNull();
        verify(findSnackPort).findById(SNACK_ID_2);
        verifyNoMoreInteractions(findSnackPort);
        verifyNoInteractions(findSaleLineSnackPort);
    }
}
