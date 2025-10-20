

package com.sap.sales_service.sale.application.usecases.find;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.factory.SaleFactory;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.usecases.find.dtos.SaleFilterDTO;
import com.sap.sales_service.sale.domain.Sale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindSaleCaseTest {

    @Mock
    private FindSalePort findSalePort;

    @Mock
    private SaleFactory saleFactory;

    @InjectMocks
    private FindSaleCase findSaleCase;

    @Test
    void findSaleById_shouldReturnSaleWithRelations_whenFound() {
        UUID id = UUID.randomUUID();
        Sale raw = mock(Sale.class);
        Sale enriched = mock(Sale.class);

        given(findSalePort.findById(id)).willReturn(Optional.of(raw));
        given(saleFactory.saleWithAllRelations(raw)).willReturn(enriched);

        var result = findSaleCase.findSaleById(id);

        assertThat(result).isSameAs(enriched);
        verify(findSalePort).findById(id);
        verify(saleFactory).saleWithAllRelations(raw);
    }

    @Test
    void findSaleById_shouldThrowNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        given(findSalePort.findById(id)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> findSaleCase.findSaleById(id));
        verify(findSalePort).findById(id);
        verifyNoMoreInteractions(saleFactory);
    }

    @Test
    void findSalesByCustomerId_shouldSearchAndMap() {
        UUID customerId = UUID.randomUUID();
        int page = 0;
        SaleFilterDTO dto = mock(SaleFilterDTO.class);
        Page<Sale> portPage = new PageImpl<>(List.of(mock(Sale.class)));

        given(findSalePort.search(any(), eq(page))).willReturn(portPage);
        given(saleFactory.salesWithAllRelations(portPage)).willReturn(portPage);

        var result = findSaleCase.findSalesByCustomerId(customerId, dto, page);

        assertThat(result).isSameAs(portPage);
        // ensure the DTO was used to build a filter
        verify(dto).withClientId(customerId);
        verify(findSalePort).search(any(), eq(page));
        verify(saleFactory).salesWithAllRelations(portPage);
    }

    @Test
    void findSalesByCinemaId_shouldSearchAndMap() {
        UUID cinemaId = UUID.randomUUID();
        int page = 1;
        SaleFilterDTO dto = mock(SaleFilterDTO.class);
        Page<Sale> portPage = new PageImpl<>(List.of(mock(Sale.class)));

        given(findSalePort.search(any(), eq(page))).willReturn(portPage);
        given(saleFactory.salesWithAllRelations(portPage)).willReturn(portPage);

        var result = findSaleCase.findSalesByCinemaId(cinemaId, dto, page);

        assertThat(result).isSameAs(portPage);
        verify(dto).withCinemaId(cinemaId);
        verify(findSalePort).search(any(), eq(page));
        verify(saleFactory).salesWithAllRelations(portPage);
    }

    @Test
    void findAllSales_shouldSearchAndMap() {
        int page = 2;
        SaleFilterDTO dto = mock(SaleFilterDTO.class);
        Page<Sale> portPage = new PageImpl<>(List.of(mock(Sale.class)));

        given(findSalePort.search(any(), eq(page))).willReturn(portPage);
        given(saleFactory.salesWithAllRelations(portPage)).willReturn(portPage);

        var result = findSaleCase.findAllSales(dto, page);

        assertThat(result).isSameAs(portPage);
        verify(dto).toDomain();
        verify(findSalePort).search(any(), eq(page));
        verify(saleFactory).salesWithAllRelations(portPage);
    }
}