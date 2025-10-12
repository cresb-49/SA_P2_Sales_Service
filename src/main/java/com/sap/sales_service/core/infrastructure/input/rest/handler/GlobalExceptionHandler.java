package com.sap.sales_service.core.infrastructure.input.rest.handler;

import com.sap.common_lib.dto.response.RestApiErrorDTO;
import com.sap.common_lib.exception.EntityAlreadyExistsException;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.common_lib.util.ValidationErrorExtractor;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public RestApiErrorDTO handleEntityAlreadyExists(EntityAlreadyExistsException ex) {
        return new RestApiErrorDTO(List.of(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public RestApiErrorDTO handleIllegalStateException(IllegalStateException ex) {
        return new RestApiErrorDTO(List.of(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public RestApiErrorDTO handleNotFoundException(NotFoundException ex) {
        return new RestApiErrorDTO(List.of(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestApiErrorDTO handleIllegalArgumentException(IllegalArgumentException ex) {
        return new RestApiErrorDTO(List.of(ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestApiErrorDTO handleValidationExceptions(ConstraintViolationException ex) {
        List<String> errors = ValidationErrorExtractor.extractErrors(ex);
        return new RestApiErrorDTO(errors);
    }

    /*
     * si para la comunicacion entre micros estan usando:<dependency>
     * <groupId>org.springframework.boot</groupId>
     * <artifactId>spring-boot-starter-webflux</artifactId>
     * </dependency>
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<?> handleWebClientResponse(WebClientResponseException ex) {
        // si el status es 200 pero falló algo en este micro
        if (ex.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RestApiErrorDTO(List.of("Error interno al procesar la respuesta del servicio remoto")));
        }

        // si es 4xx o 5xx propaga tal cual el error remoto
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(ex.getHeaders().getContentType())
                .body(ex.getResponseBodyAsString());
    }

}
