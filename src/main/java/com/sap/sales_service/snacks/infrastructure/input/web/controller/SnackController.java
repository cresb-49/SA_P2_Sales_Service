package com.sap.sales_service.snacks.infrastructure.input.web.controller;

import com.sap.sales_service.snacks.application.input.CreateSnackPort;
import com.sap.sales_service.snacks.application.input.FindSnackPort;
import com.sap.sales_service.snacks.application.input.UpdateSnackPort;
import com.sap.sales_service.snacks.domain.SnackFilter;
import com.sap.sales_service.snacks.infrastructure.input.web.dtos.CreateSnackRequestDTO;
import com.sap.sales_service.snacks.infrastructure.input.web.dtos.UpdateSnackRequestDTO;
import com.sap.sales_service.snacks.infrastructure.input.web.mappers.SnackResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import com.sap.common_lib.dto.response.RestApiErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/api/v1/snacks")
@AllArgsConstructor
@Tag(name = "Snacks", description = "Endpoints públicos y protegidos para gestionar snacks")
public class SnackController {

    private final CreateSnackPort createSnackPort;
    private final UpdateSnackPort updateSnackPort;
    private final FindSnackPort findSnackPort;

    private final SnackResponseMapper snackResponseMapper;

    //Public endpoints
    @Operation(summary = "Obtener snack por ID (público)", description = "Recupera la información de un snack por su identificador.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del snack", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Snack encontrado"),
            @ApiResponse(responseCode = "404", description = "Snack no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getById(
            @PathVariable UUID id
    ) {
        var snack = findSnackPort.findById(id);
        var response = snackResponseMapper.toResponse(snack);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear snack", description = "Crea un nuevo snack. Puede incluir un archivo multimedia opcional.")
    @Parameters({
            @Parameter(name = "file", description = "Archivo multimedia opcional (imagen)")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Snack creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto: el recurso ya existe", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN')")
    public ResponseEntity<?> createSnack(
            @ModelAttribute CreateSnackRequestDTO requestDTO,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        var snack = createSnackPort.create(requestDTO.toDomain(file));
        var response = snackResponseMapper.toResponse(snack);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar snack", description = "Actualiza la información de un snack. Puede incluir un archivo multimedia opcional.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del snack", required = true),
            @Parameter(name = "file", description = "Archivo multimedia opcional (imagen)")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Snack actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Snack no encontrado", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto de actualización", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CINEMA_ADMIN')")
    public ResponseEntity<?> updateSnack(
            @PathVariable UUID id,
            @ModelAttribute UpdateSnackRequestDTO requestDTO,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        var snack = updateSnackPort.update(requestDTO.toDomain(id, file));
        var response = snackResponseMapper.toResponse(snack);
        return ResponseEntity.ok(response);
    }

    // Public endpoint to get snacks by name (partial match) with pagination
    @Operation(summary = "Buscar snacks (público)", description = "Busca snacks por nombre (coincidencia parcial), activo y cine. Devuelve página de resultados.")
    @Parameters({
            @Parameter(name = "name", description = "Nombre o parte del nombre del snack"),
            @Parameter(name = "active", description = "Si el snack está activo"),
            @Parameter(name = "cinemaId", description = "Identificador del cine"),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de snacks recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/public/search")
    public ResponseEntity<?> getSnacksByName(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var filter = new SnackFilter(name, active, cinemaId);
        var snacks = findSnackPort.search(filter, page);
        var response = snackResponseMapper.toPageResponse(snacks);
        return ResponseEntity.ok(response);
    }

    // Public endpoint to get snacks by a list of ids
    @Operation(summary = "Obtener snacks por IDs (público)", description = "Recupera una lista de snacks a partir de sus identificadores.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de snacks recuperado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @PostMapping("/public/ids")
    public ResponseEntity<?> getSnacksByIds(@RequestBody List<UUID> ids) {
        var snacks = findSnackPort.findByIds(ids);
        var response = snackResponseMapper.toListResponse(snacks);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar snacks por cine (público)", description = "Lista snacks filtrando por cine y parámetros opcionales.")
    @Parameters({
            @Parameter(name = "cinemaId", description = "Identificador del cine", required = true),
            @Parameter(name = "name", description = "Nombre o parte del nombre del snack"),
            @Parameter(name = "active", description = "Si el snack está activo"),
            @Parameter(name = "page", description = "Número de página (0-index)", example = "0")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de snacks recuperada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = RestApiErrorDTO.class)))
    })
    @GetMapping("/public/cinema/{cinemaId}")
    public ResponseEntity<?> getSnacksByCinemaId(
            @PathVariable UUID cinemaId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page
    ) {
        var filter = new SnackFilter(name, active, cinemaId);
        var snacks = findSnackPort.search(filter, page);
        var response = snackResponseMapper.toPageResponse(snacks);
        return ResponseEntity.ok(response);
    }
}
