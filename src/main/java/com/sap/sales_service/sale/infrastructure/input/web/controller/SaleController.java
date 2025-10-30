package com.sap.sales_service.sale.infrastructure.input.web.controller;

import com.sap.common_lib.dto.response.RestApiErrorDTO;
import com.sap.sales_service.sale.application.input.*;
import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleDTO;
import com.sap.sales_service.sale.application.usecases.find.dtos.SaleFilterDTO;
import com.sap.sales_service.sale.infrastructure.input.web.mapper.SaleResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Ventas", description = "Endpoints para gestionar ventas y su estado de pago")
@SecurityRequirement(name = "bearerAuth")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sales")
public class SaleController {
    //Use cases will be implemented here
    private final FindSaleCasePort findSaleCasePort;
    private final CreateSaleCasePort createSaleCasePort;
    private final ClaimTicketMoneySaleLineCasePort claimTicketMoneySaleLineCasePort;
    private final RetryPaidSaleCasePort retryPaidSaleCasePort;
    private final SnackReportByCinemaCasePort snackReportByCinemaCasePort;
    //Mapper
    private final SaleResponseMapper saleResponseMapper;

    @Operation(summary = "Crear venta", description = "Crea una venta con líneas de boletos y/o snacks.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto al crear la venta", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<?> createSale(@RequestBody CreateSaleDTO createSaleDTO) {
        var sale = createSaleCasePort.createSale(createSaleDTO);
        var responseDTO = saleResponseMapper.toResponseDTO(sale);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Obtener venta por ID", description = "Recupera el detalle de una venta por su identificador.")
    @Parameters({
            @Parameter(name = "saleId", description = "Identificador de la venta", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/{saleId}")
    public ResponseEntity<?> getSaleById(@PathVariable UUID saleId) {
        var sale = findSaleCasePort.findSaleById(saleId);
        var responseDTO = saleResponseMapper.toResponseDTO(sale);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Listar ventas por cine", description = "Retorna una página de ventas filtradas por cine y parámetros opcionales.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de ventas recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<?> getSalesByCinemaId(
            @PathVariable UUID cinemaId,
            @ModelAttribute SaleFilterDTO saleFilterDTO,
            @RequestParam(defaultValue = "0") int page
    ) {
        var sales = findSaleCasePort.findSalesByCinemaId(cinemaId, saleFilterDTO, page);
        var responseDTO = sales.map(saleResponseMapper::toResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Listar ventas por cliente", description = "Retorna una página de ventas filtradas por cliente y parámetros opcionales.")
    @Parameters({
            @Parameter(name = "customerId", description = "Identificador del cliente", required = true),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de ventas recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<?> getSalesByCustomerId(
            @PathVariable UUID customerId,
            @ModelAttribute SaleFilterDTO saleFilterDTO,
            @RequestParam(defaultValue = "0") int page
    ) {
        var sales = findSaleCasePort.findSalesByCustomerId(customerId, saleFilterDTO, page);
        var responseDTO = sales.map(saleResponseMapper::toResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Listar todas las ventas", description = "Retorna una página con todas las ventas aplicando filtros opcionales.")
    @Parameters({
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de ventas recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN')")
    public ResponseEntity<?> getAllSales(
            @ModelAttribute SaleFilterDTO saleFilterDTO,
            @RequestParam(defaultValue = "0") int page
    ) {
        var sales = findSaleCasePort.findAllSales(saleFilterDTO, page);
        var responseDTO = sales.map(saleResponseMapper::toResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Reclamar dinero por boleto", description = "Ejecuta el reclamo del dinero asociado a una línea de ticket de venta.")
    @Parameters({
            @Parameter(name = "saleLineTicketId", description = "Identificador de la línea de ticket", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reclamo procesado"),
            @ApiResponse(responseCode = "404", description = "Línea de ticket no encontrada", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto al procesar el reclamo", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/claim/sale-line-ticket/{saleLineTicketId}")
    public ResponseEntity<?> claimTicketMoney(@PathVariable UUID saleLineTicketId) {
        claimTicketMoneySaleLineCasePort.claimTicketMoneySaleLine(saleLineTicketId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reintentar confirmación de pago de venta", description = "Reintenta la confirmación/cambio de estado de pago para la venta indicada.")
    @Parameters({
            @Parameter(name = "saleId", description = "Identificador de la venta", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reintento ejecutado"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto al reintentar el pago", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/retry/sale/{saleId}")
    public ResponseEntity<?> retryPaidSale(@PathVariable UUID saleId) {
        retryPaidSaleCasePort.retryPaidSale(saleId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generar reporte de snacks por cine", description = "Genera un reporte resumen de ventas de snacks para un cine en un rango de fechas.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true),
            @Parameter(name = "from", description = "Fecha inicial (YYYY-MM-DD)", required = true),
            @Parameter(name = "to", description = "Fecha final (YYYY-MM-DD)", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/reports/sales/snacks/cinema/{cinemaId}")
    public ResponseEntity<?> generateSnackReportByCinema(
            @PathVariable UUID cinemaId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        var reportDTO = snackReportByCinemaCasePort.report(from, to, cinemaId);
        return ResponseEntity.ok(reportDTO);
    }

    @Operation(summary = "Descargar reporte de snacks (PDF)", description = "Genera y descarga el reporte de snacks en formato PDF para un cine en un rango de fechas.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true),
            @Parameter(name = "from", description = "Fecha inicial (YYYY-MM-DD)", required = true),
            @Parameter(name = "to", description = "Fecha final (YYYY-MM-DD)", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF generado correctamente", content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/reports/sales/snacks/cinema/{cinemaId}/pdf")
    public ResponseEntity<?> downloadSnackReportByCinemaPdf(
            @PathVariable UUID cinemaId,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        var pdfBytes = snackReportByCinemaCasePort.generateReportFile(from, to, cinemaId);
        String fileName = "snack_report_cinema_" + cinemaId + ".pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
