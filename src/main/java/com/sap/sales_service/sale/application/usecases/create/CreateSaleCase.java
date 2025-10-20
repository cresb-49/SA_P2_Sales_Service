package com.sap.sales_service.sale.application.usecases.create;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.factory.SaleFactory;
import com.sap.sales_service.sale.application.input.CreateSaleCasePort;
import com.sap.sales_service.sale.application.ouput.*;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleDTO;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleLineSnackDTO;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleLineTicketDTO;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.events.CreateTicketEventDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreateSaleCase implements CreateSaleCasePort {

    private final SaveSalePort saveSalePort;
    private final SaveSaleLineTicketPort saveSaleLineTicketPort;
    private final SaveSaleLineSnackPort saveSaleLineSnackPort;
    private final SaleFactory saleFactory;
    private final FindSnackPort findSnackPort;
    private final FindFunctionPort findFunctionPort;
    private final SendTicketRequestPort sendTicketRequestPort;
    private final SendPaidRequestPort sendPaidRequestPort;
    private final FindCinemaPort findCinemaPort;
    private final FindUserPort findUserPort;

    /**
     * Creates a new Sale based on the provided CreateSaleDTO
     *
     * @param createSaleDTO Data Transfer Object containing sale details
     * @return Created Sale with all relations
     */
    @Override
    public Sale createSale(CreateSaleDTO createSaleDTO) {
        //Verify cinema exists
        if (!findCinemaPort.existsById(createSaleDTO.cinemaId())) {
            throw new NotFoundException("El cine con id " + createSaleDTO.cinemaId() + " no fue encontrado");
        }
        //Verify client exists
        if (!findUserPort.existsById(createSaleDTO.clientId())) {
            throw new NotFoundException("El cliente con id " + createSaleDTO.clientId() + " no fue encontrado");
        }
        //Find functions
        var functions = this.getFunctionsAndValidateCinema(
                createSaleDTO.tickets(),
                createSaleDTO.cinemaId()
        );
        // Find snacks
        var snacks = this.getSnacksAndValidateCinema(
                createSaleDTO.snacks(),
                createSaleDTO.cinemaId()
        );
        //Map functions by ID
        var functionMap = this.mapFunctionsById(functions);
        // Map snacks by ID
        var snackMap = this.mapSnacksById(snacks);
        // Create Sale Lines Tickets and Snacks
        var saleLinesTickets = this.createSaleLinesTicketsAndEvents(
                createSaleDTO.tickets(),
                functionMap
        );
        // Get only SaleLineTickets
        var saleLinesTicketsList = saleLinesTickets.keySet().stream().toList();
        var saleLineSnacks = this.createSaleLinesSnacks(
                createSaleDTO.snacks(),
                snackMap
        );
        // Create Sale
        var sale = new Sale(
                createSaleDTO.clientId(),
                createSaleDTO.cinemaId(),
                BigDecimal.ZERO,
                saleLineSnacks,
                saleLinesTicketsList
        );
        //Validate Sale
        sale.validate();
        // Save Sale
        var savedSale = saveSalePort.save(sale);
        // Save Sale Lines Tickets
        this.saveSaleLinesTickets(saleLinesTicketsList);
        // Save Sale Lines Snacks
        this.saveSaleLinesSnacks(saleLineSnacks);
        // Send messages to create a ticket
        var ticketEvents = saleLinesTickets.values().stream().toList();
        // send ticket requests
        this.sendTicketRequests(ticketEvents);
        // Send paid request
        var paidAmount = savedSale.getPayableAmount();
        sendPaidRequestPort.sendPaidRequest(savedSale.getClientId(), savedSale.getId(), paidAmount);
        // Return Sale with all relations
        return saleFactory.saleWithAllRelations(savedSale);
    }

    // Helper methods

    private List<SnackView> getSnacksAndValidateCinema(
            List<CreateSaleLineSnackDTO> snackDTOs,
            UUID cinemaId
    ) {
        // Get snack UUIDs
        var snackUUIDs = snackDTOs.stream().map(
                CreateSaleLineSnackDTO::snackId
        ).distinct().toList();
        //Find snacks
        var snacks = findSnackPort.findAllById(snackUUIDs);
        if (snacks.isEmpty() || (snacks.size() != snackUUIDs.size())) {
            throw new IllegalStateException("Uno o más snacks no fueron encontrados");
        }
        // Validate snacks belong to cinema
        for (SnackView snack : snacks) {
            if (snack.cinemaId() != cinemaId) {
                throw new IllegalStateException("El snack con id " + snack.id() + " no pertenece al cine con id " + cinemaId);
            }
        }
        return snacks;
    }

    /**
     * Retrieves functions and validates that they belong to the specified cinema
     *
     * @param ticketDTOs List of CreateSaleLineTicketDTO
     * @param cinemaId   UUID of the cinema
     * @return List of FunctionView
     */
    private List<FunctionView> getFunctionsAndValidateCinema(
            List<CreateSaleLineTicketDTO> ticketDTOs,
            UUID cinemaId
    ) {
        // Get function UUIDs
        var functionUUIDs = ticketDTOs.stream().map(
                CreateSaleLineTicketDTO::cinemaFunctionId
        ).distinct().toList();
        //Find functions
        var functions = findFunctionPort.findByFunctionIds(functionUUIDs);
        if (functions.isEmpty() || (functions.size() != functionUUIDs.size())) {
            throw new IllegalStateException("Una o más funciones no fueron encontradas");
        }
        // Verificamos que las funciones pertenezcan al cine indicado
        for (FunctionView function : functions) {
            if (function.cinemaId() != cinemaId) {
                throw new IllegalStateException("La función con id " + function.id() + " no pertenece al cine con id " + cinemaId);
            }
        }
        return functions;
    }

    /**
     * Sends ticket requests for a list of CreateTicketEventDTO
     *
     * @param ticketEvents List of CreateTicketEventDTO
     */
    private void sendTicketRequests(List<CreateTicketEventDTO> ticketEvents) {
        for (CreateTicketEventDTO ticketEvent : ticketEvents) {
            sendTicketRequestPort.sendTicketRequest(ticketEvent);
        }
    }

    /**
     * Saves a list of SaleLineTicket
     *
     * @param saleLineTickets List of SaleLineTicket
     */
    private void saveSaleLinesTickets(List<SaleLineTicket> saleLineTickets) {
        for (SaleLineTicket lineTicket : saleLineTickets) {
            saveSaleLineTicketPort.save(lineTicket);
        }
    }

    /**
     * Saves a list of SaleLineSnack
     *
     * @param saleLineSnacks List of SaleLineSnack
     */
    private void saveSaleLinesSnacks(List<SaleLineSnack> saleLineSnacks) {
        for (SaleLineSnack lineSnack : saleLineSnacks) {
            saveSaleLineSnackPort.save(lineSnack);
        }
    }

    /**
     * Maps a list of SnackView to a Map with UUID as key and SnackView as value
     *
     * @param snacks List of SnackView
     * @return Map of SnackView by UUID
     */
    private Map<UUID, SnackView> mapSnacksById(List<SnackView> snacks) {
        return snacks.stream().collect(
                java.util.stream.Collectors.toMap(
                        SnackView::id,
                        s -> s
                )
        );
    }

    /**
     * Maps a list of FunctionView to a Map with UUID as key and FunctionView as value
     *
     * @param functions List of FunctionView
     * @return Map of FunctionView by UUID
     */
    private Map<UUID, FunctionView> mapFunctionsById(List<FunctionView> functions) {
        return functions.stream().collect(
                java.util.stream.Collectors.toMap(
                        FunctionView::id,
                        f -> f
                )
        );
    }

    /**
     * Creates SaleLineTicket list from CreateSaleLineTicketDTO list
     *
     * @param ticketDTOs  List of CreateSaleLineTicketDTO
     * @param functionMap Map of FunctionView by UUID
     * @return List of SaleLineTicket
     */
    private Map<SaleLineTicket, CreateTicketEventDTO> createSaleLinesTicketsAndEvents(
            List<CreateSaleLineTicketDTO> ticketDTOs,
            Map<UUID, FunctionView> functionMap
    ) {
        return ticketDTOs.stream().map(
                ticketDTO -> {
                    var function = functionMap.get(ticketDTO.cinemaFunctionId());
                    var saleLineTicket = new SaleLineTicket(
                            null,
                            1,
                            function.price()
                    );
                    var ticketEventDTO = new CreateTicketEventDTO(
                            saleLineTicket.getId(),
                            function.id(),
                            function.cinemaId(),
                            function.cinemaRoomId(),
                            ticketDTO.seatId(),
                            function.movieId()
                    );
                    return Map.entry(saleLineTicket, ticketEventDTO);
                }
        ).collect(
                java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                )
        );
    }

    /**
     * Creates SaleLineSnack list from CreateSaleLineSnackDTO list
     *
     * @param snackDTOs List of CreateSaleLineSnackDTO
     * @param snackMap  Map of SnackView by UUID
     * @return List of SaleLineSnack
     */
    private List<SaleLineSnack> createSaleLinesSnacks(
            List<CreateSaleLineSnackDTO> snackDTOs,
            Map<UUID, SnackView> snackMap
    ) {
        return snackDTOs.stream().map(
                snackDTO -> {
                    return new SaleLineSnack(
                            null,
                            snackDTO.snackId(),
                            snackDTO.quantity(),
                            snackMap.get(snackDTO.snackId()).price()
                    );
                }
        ).toList();
    }
}
