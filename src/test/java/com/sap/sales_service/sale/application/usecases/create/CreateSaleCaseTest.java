package com.sap.sales_service.sale.application.usecases.create;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.factory.SaleFactory;
import com.sap.sales_service.sale.application.ouput.*;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleDTO;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleLineSnackDTO;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleLineTicketDTO;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.events.CreateTicketInternalViewEventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSaleCaseTest {

    private static final UUID CLIENT_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID CINEMA_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FUNCTION_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID MOVIE_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final UUID CINEMA_ROOM_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    private static final UUID SNACK_ID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    @Mock
    private SaveSalePort saveSalePort;
    @Mock
    private SaveSaleLineTicketPort saveSaleLineTicketPort;
    @Mock
    private SaveSaleLineSnackPort saveSaleLineSnackPort;
    @Mock
    private SaleFactory saleFactory;
    @Mock
    private FindSnackPort findSnackPort;
    @Mock
    private FindFunctionPort findFunctionPort;
    @Mock
    private SendTicketRequestPort sendTicketRequestPort;
    @Mock
    private SendPaidRequestPort sendPaidRequestPort;
    @Mock
    private FindCinemaPort findCinemaPort;
    @Mock
    private FindUserPort findUserPort;

    @InjectMocks
    private CreateSaleCase createSaleCase;

    @Test
    void createSale_shouldPersistEntitiesAndSendEvents_whenRequestValid() {
        // Arrange
        var ticketDTO = new CreateSaleLineTicketDTO(FUNCTION_ID);
        var snackDTO = new CreateSaleLineSnackDTO(SNACK_ID, 2);
        var request = new CreateSaleDTO(
                CLIENT_ID,
                CINEMA_ID,
                List.of(snackDTO),
                List.of(ticketDTO)
        );

        var functionView = new FunctionView(
                FUNCTION_ID,
                MOVIE_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                new BigDecimal("20.00"),
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 12, 0),
                100
        );

        var snackView = new SnackView(
                SNACK_ID,
                CINEMA_ID,
                "Pop Corn",
                new BigDecimal("5.00"),
                "http://image.png",
                LocalDateTime.of(2024, 1, 1, 9, 0),
                LocalDateTime.of(2024, 1, 1, 9, 30)
        );

        when(findCinemaPort.existsById(CINEMA_ID)).thenReturn(true);
        when(findUserPort.existsById(CLIENT_ID)).thenReturn(true);
        when(findFunctionPort.findByFunctionIds(List.of(FUNCTION_ID))).thenReturn(List.of(functionView));
        when(findSnackPort.findAllById(List.of(SNACK_ID))).thenReturn(List.of(snackView));
        when(saveSalePort.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var enrichedSale = mock(Sale.class);
        when(saleFactory.saleWithAllRelations(any(Sale.class))).thenReturn(enrichedSale);

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        ArgumentCaptor<SaleLineTicket> ticketCaptor = ArgumentCaptor.forClass(SaleLineTicket.class);
        ArgumentCaptor<SaleLineSnack> snackCaptor = ArgumentCaptor.forClass(SaleLineSnack.class);
        ArgumentCaptor<CreateTicketInternalViewEventDTO> eventCaptor = ArgumentCaptor.forClass(CreateTicketInternalViewEventDTO.class);
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        // Act
        var result = createSaleCase.createSale(request);

        // Assert
        assertThat(result).isEqualTo(enrichedSale);

        verify(saveSalePort).save(saleCaptor.capture());
        var persistedSale = saleCaptor.getValue();
        assertThat(persistedSale.getClientId()).isEqualTo(CLIENT_ID);
        assertThat(persistedSale.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30.00"));

        verify(saveSaleLineTicketPort).save(ticketCaptor.capture());
        var persistedTicket = ticketCaptor.getValue();
        assertThat(persistedTicket.getSaleId()).isNotNull();
        assertThat(persistedTicket.getTotalPrice()).isEqualByComparingTo(new BigDecimal("20.00"));

        verify(saveSaleLineSnackPort).save(snackCaptor.capture());
        var persistedSnack = snackCaptor.getValue();
        assertThat(persistedSnack.getSaleId()).isNotNull();
        assertThat(persistedSnack.getTotalPrice()).isEqualByComparingTo(new BigDecimal("10.00"));

        verify(sendTicketRequestPort).sendTicketRequest(eventCaptor.capture());
        var event = eventCaptor.getValue();
        assertThat(event.cinemaFunctionId()).isEqualTo(FUNCTION_ID);
        assertThat(event.cinemaId()).isEqualTo(CINEMA_ID);
        assertThat(event.movieId()).isEqualTo(MOVIE_ID);

        verify(sendPaidRequestPort).sendPaidRequest(eq(CLIENT_ID), eq(CINEMA_ID), eq(persistedSale.getId()), amountCaptor.capture());
        assertThat(amountCaptor.getValue()).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    void createSale_shouldThrowNotFound_whenCinemaDoesNotExist() {
        var request = new CreateSaleDTO(
                CLIENT_ID,
                CINEMA_ID,
                List.of(),
                List.of()
        );

        when(findCinemaPort.existsById(CINEMA_ID)).thenReturn(false);

        assertThatThrownBy(() -> createSaleCase.createSale(request))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(saveSalePort, saveSaleLineTicketPort, saveSaleLineSnackPort, sendTicketRequestPort, sendPaidRequestPort, saleFactory);
    }

    @Test
    void createSale_shouldThrowNotFound_whenUserDoesNotExist() {
        var request = new CreateSaleDTO(
                CLIENT_ID,
                CINEMA_ID,
                List.of(),
                List.of()
        );

        when(findCinemaPort.existsById(CINEMA_ID)).thenReturn(true);
        when(findUserPort.existsById(CLIENT_ID)).thenReturn(false);

        assertThatThrownBy(() -> createSaleCase.createSale(request))
                .isInstanceOf(NotFoundException.class);

        verify(findFunctionPort, never()).findByFunctionIds(any());
        verifyNoInteractions(saveSalePort);
    }

    @Test
    void createSale_shouldThrow_whenFunctionsNotFound() {
        var ticketDTO = new CreateSaleLineTicketDTO(FUNCTION_ID);
        var request = new CreateSaleDTO(
                CLIENT_ID,
                CINEMA_ID,
                List.of(),
                List.of(ticketDTO)
        );

        when(findCinemaPort.existsById(CINEMA_ID)).thenReturn(true);
        when(findUserPort.existsById(CLIENT_ID)).thenReturn(true);
        when(findFunctionPort.findByFunctionIds(List.of(FUNCTION_ID))).thenReturn(List.of());

        assertThatThrownBy(() -> createSaleCase.createSale(request))
                .isInstanceOf(IllegalStateException.class);

        verify(findSnackPort, never()).findAllById(anyList());
        verifyNoInteractions(saveSalePort);
    }

    @Test
    void createSale_shouldThrow_whenSnacksDoNotBelongToCinema() {
        var ticketDTO = new CreateSaleLineTicketDTO(FUNCTION_ID);
        var snackDTO = new CreateSaleLineSnackDTO(SNACK_ID, 1);
        var request = new CreateSaleDTO(
                CLIENT_ID,
                CINEMA_ID,
                List.of(snackDTO),
                List.of(ticketDTO)
        );

        var functionView = new FunctionView(
                FUNCTION_ID,
                MOVIE_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                new BigDecimal("15.00"),
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 11, 0),
                50
        );
        var snackView = new SnackView(
                SNACK_ID,
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                "Hot Dog",
                new BigDecimal("4.00"),
                "http://image.png",
                LocalDateTime.of(2024, 1, 1, 9, 0),
                LocalDateTime.of(2024, 1, 1, 9, 30)
        );

        when(findCinemaPort.existsById(CINEMA_ID)).thenReturn(true);
        when(findUserPort.existsById(CLIENT_ID)).thenReturn(true);
        when(findFunctionPort.findByFunctionIds(List.of(FUNCTION_ID))).thenReturn(List.of(functionView));
        when(findSnackPort.findAllById(List.of(SNACK_ID))).thenReturn(List.of(snackView));

        assertThatThrownBy(() -> createSaleCase.createSale(request))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(saveSalePort);
    }
}
