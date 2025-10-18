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

@Controller
@RequestMapping("/api/v1/snacks")
@AllArgsConstructor
public class SnackController {

    private final CreateSnackPort createSnackPort;
    private final UpdateSnackPort updateSnackPort;
    private final FindSnackPort findSnackPort;

    private final SnackResponseMapper snackResponseMapper;

    //Public endpoints
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getById(
            @PathVariable UUID id
    ) {
        var snack = findSnackPort.findById(id);
        var response = snackResponseMapper.toResponse(snack);
        return ResponseEntity.ok(response);
    }

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
    @PostMapping("/public/ids")
    public ResponseEntity<?> getSnacksByIds(@RequestBody List<UUID> ids) {
        var snacks = findSnackPort.findByIds(ids);
        var response = snackResponseMapper.toListResponse(snacks);
        return ResponseEntity.ok(response);
    }

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
