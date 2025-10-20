package com.sap.sales_service.sale.infrastructure.input.web.controller;

import com.sap.sales_service.sale.application.input.ClaimTicketMoneySaleLineCasePort;
import com.sap.sales_service.sale.application.input.CreateSaleCasePort;
import com.sap.sales_service.sale.application.input.FindSaleCasePort;
import com.sap.sales_service.sale.application.input.RetryPaidSaleCasePort;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleDTO;
import com.sap.sales_service.sale.application.usecases.find.dtos.SaleFilterDTO;
import com.sap.sales_service.sale.infrastructure.input.web.mapper.SaleResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sales")
public class SaleController {
    //Use cases will be implemented here
    private final FindSaleCasePort findSaleCasePort;
    private final CreateSaleCasePort createSaleCasePort;
    private final ClaimTicketMoneySaleLineCasePort claimTicketMoneySaleLineCasePort;
    private final RetryPaidSaleCasePort retryPaidSaleCasePort;
    //Mapper
    private final SaleResponseMapper saleResponseMapper;


    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody CreateSaleDTO createSaleDTO) {
        var sale = createSaleCasePort.createSale(createSaleDTO);
        var responseDTO = saleResponseMapper.toResponseDTO(sale);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<?> getSaleById(@PathVariable UUID saleId) {
        var sale = findSaleCasePort.findSaleById(saleId);
        var responseDTO = saleResponseMapper.toResponseDTO(sale);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<?> getSalesByCinemaId(
            @PathVariable UUID cinemaId,
            @ModelAttribute SaleFilterDTO saleFilterDTO,
            @RequestParam(defaultValue = "0") int page
    ) {
        var sales = findSaleCasePort.findSalesByCinemaId(cinemaId, saleFilterDTO, page);
        var responseDTO = sales.map(saleResponseMapper::toResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getSalesByCustomerId(
            @PathVariable UUID customerId,
            @ModelAttribute SaleFilterDTO saleFilterDTO,
            @RequestParam(defaultValue = "0") int page
    ) {
        var sales = findSaleCasePort.findSalesByCustomerId(customerId, saleFilterDTO, page);
        var responseDTO = sales.map(saleResponseMapper::toResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSales(
            @ModelAttribute SaleFilterDTO saleFilterDTO,
            @RequestParam(defaultValue = "0") int page
    ) {
        var sales = findSaleCasePort.findAllSales(saleFilterDTO, page);
        var responseDTO = sales.map(saleResponseMapper::toResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/claim/sale-line-ticket/{saleLineTicketId}")
    public ResponseEntity<?> claimTicketMoney(@PathVariable UUID saleLineTicketId) {
        claimTicketMoneySaleLineCasePort.claimTicketMoneySaleLine(saleLineTicketId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/retry/sale/{saleId}")
    public ResponseEntity<?> retryPaidSale(@PathVariable UUID saleId) {
        retryPaidSaleCasePort.retryPaidSale(saleId);
        return ResponseEntity.ok().build();
    }
}
