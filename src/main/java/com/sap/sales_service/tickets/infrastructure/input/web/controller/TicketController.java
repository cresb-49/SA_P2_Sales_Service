package com.sap.sales_service.tickets.infrastructure.input.web.controller;

import com.sap.common_lib.dto.response.RestApiErrorDTO;
import com.sap.sales_service.tickets.application.input.FindTicketPort;
import com.sap.sales_service.tickets.application.input.GetOccupiedSetsByCinemaFunctionPort;
import com.sap.sales_service.tickets.application.input.MarkUsedTicketPort;
import com.sap.sales_service.tickets.infrastructure.input.web.mapper.TicketResponseMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@AllArgsConstructor
@Tag(name = "Tickets", description = "Endpoints para gestionar tickets y ocupación de asientos")
public class TicketController {

    private final FindTicketPort findTicketPort;
    private final MarkUsedTicketPort markUsedTicketPort;
    private final TicketResponseMapper ticketResponseMapper;
    private final GetOccupiedSetsByCinemaFunctionPort getOccupiedSetsByCinemaFunctionPort;

    @Operation(summary = "Obtener ticket por ID", description = "Recupera la información de un ticket por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del ticket", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable UUID id) {
        var ticket = findTicketPort.findById(id);
        var responseDTO = ticketResponseMapper.toResponseDTO(ticket);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Marcar ticket como usado", description = "Cambia el estado del ticket a usado.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del ticket", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto al actualizar el estado del ticket", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/mark-used/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<?> markTicketAsUsed(@PathVariable UUID id) {
        markUsedTicketPort.markTicketAsUsed(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cantidad de asientos ocupados (público)", description = "Devuelve la cantidad de asientos ocupados para una función de cine específica.")
    @Parameters({
            @Parameter(name = "cinemaFunctionId", description = "Identificador de la función de cine", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de asientos ocupado recuperado"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/public/cinema-function/{cinemaFunctionId}/seats/occupied/ids")
    public ResponseEntity<QuantityResponseDTO> isSeatOccupied(@PathVariable UUID cinemaFunctionId) {
        var occupiedSeats = getOccupiedSetsByCinemaFunctionPort.getOccupiedSeatsByCinemaFunctionId(cinemaFunctionId);
        return ResponseEntity.ok(new QuantityResponseDTO(occupiedSeats));
    }

    public record QuantityResponseDTO(Integer quantity) {
    }
}
