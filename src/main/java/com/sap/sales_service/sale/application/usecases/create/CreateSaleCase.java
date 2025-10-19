package com.sap.sales_service.sale.application.usecases.create;

import com.sap.sales_service.sale.application.factory.SaleFactory;
import com.sap.sales_service.sale.application.input.CreateSaleCasePort;
import com.sap.sales_service.sale.application.ouput.FindFunctionPort;
import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleDTO;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleLineSnackDTO;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleLineTicketDTO;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreateSaleCase implements CreateSaleCasePort {

    private final SaveSalePort saveSalePort;
    private final SaleFactory saleFactory;
    private final FindSnackPort findSnackPort;
    private final FindFunctionPort findFunctionPort;

    @Override
    public Sale createSale(CreateSaleDTO createSaleDTO) {
        //Get function UUIDs
        var functionUUIDs = createSaleDTO.tickets().stream().map(
                CreateSaleLineTicketDTO::cinemaFunctionId
        ).distinct();
        //Find functions
        var functions = findFunctionPort.findByFunctionIds(functionUUIDs.toList());
        if (functions.isEmpty() || (functions.size() != functionUUIDs.toList().size())) {
            throw new IllegalStateException("One or more functions not found");
        }
        //Get snack uuids
        var snackUUIDs = createSaleDTO.snacks().stream().map(
                CreateSaleLineSnackDTO::snackId
        ).distinct();
        // Find snacks
        var snacks = findSnackPort.findAllById(snackUUIDs.toList());
        if (snacks.isEmpty() || (snacks.size() != snackUUIDs.toList().size())) {
            throw new IllegalStateException("One or more snacks not found");
        }
        //Map functions by ID
        var functionMap = functions.stream().collect(
                java.util.stream.Collectors.toMap(
                        FunctionView::id,
                        f -> f
                )
        );
        // Map snacks by ID
        var snackMap = snacks.stream().collect(
                java.util.stream.Collectors.toMap(
                        SnackView::id,
                        s -> s
                )
        );
        // Create Sale Lines Tickets and Snacks
        var saleLines = createSaleDTO.tickets().stream().map(
                ticketDTO -> {
                    var function = functionMap.get(ticketDTO.cinemaFunctionId());
                    return new SaleLineTicket(
                            null,
                            1,
                            function.price()
                    );
                }
        ).toList();
        var saleLineSnacks = createSaleDTO.snacks().stream().map(
                snackDTO -> {
                    return new SaleLineSnack(
                            null,
                            snackDTO.snackId(),
                            snackDTO.quantity(),
                            snackMap.get(snackDTO.snackId()).price()
                    );
                }
        ).toList();
        // Create Sale
        var sale = new Sale(
                createSaleDTO.clientId(),
                saleLineSnacks,
                saleLines
        );
        //Set saleId to sale lines
        sale.getSaleLineSnacks().forEach(saleLineSnack -> {
            saleLineSnack.setSaleId(sale.getId());
        });
        sale.getSaleLineTickets().forEach(saleLineTicket -> {
            saleLineTicket.setSaleId(sale.getId());
        });
        //Validate Sale
        sale.validate();
        // Save Sale
        var savedSale = saveSalePort.save(sale);
        return saleFactory.saleWithAllRelations(savedSale);
    }
}
