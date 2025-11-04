package com.sap.sales_service.sale.domain;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class SaleLineTicket {
    private UUID id;
    @Setter
    private UUID saleId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private TicketStatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // View information of ticket if exists
    @Setter
    private TicketView ticketView;

    public SaleLineTicket(
            UUID id, UUID saleId, Integer quantity, BigDecimal unitPrice,
            BigDecimal totalPrice, TicketStatusType status,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.id = id;
        this.saleId = saleId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public SaleLineTicket(UUID saleId, Integer quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.saleId = saleId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = TicketStatusType.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (saleId == null) {
            throw new IllegalArgumentException("El ID de la venta no puede ser nulo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Las unidades de precio deben ser no negativas");
        }
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El total del precio debe ser no negativo");
        }
    }

    public void use() {
        //No importa el estado anterior la peticion de este ticken no puede aceptar ya que ese ticket ya se esta usando
        this.status = TicketStatusType.IN_USE;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!(this.status == TicketStatusType.RESERVED || this.status == TicketStatusType.PENDING)) {
            throw new RuntimeException("Solo los tickets reservados o pendientes pueden ser cancelados");
        }
        this.status = TicketStatusType.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void purchase() {
        if (!(this.status == TicketStatusType.RESERVED || this.status == TicketStatusType.PENDING)) {
            throw new RuntimeException("Los tickets solo pueden ser comprados si están en estado reservado o pendiente");
        }
        this.status = TicketStatusType.PURCHASED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reserve() {
        // Existen dos casos la linea ya esta pagada asi que no se cambia el estado
        if (this.status == TicketStatusType.PURCHASED) {
            return;
        }
        // Solo se pueden reservar tickets que esten pendientes
        if (this.status != TicketStatusType.PENDING) {
            throw new RuntimeException("Solo los tickets pendientes pueden ser reservados");
        }
        this.status = TicketStatusType.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void pend() {
        if (this.status != TicketStatusType.RESERVED) {
            throw new RuntimeException("Solo los tickets reservados pueden ser puestos en estado pendiente");
        }
        this.status = TicketStatusType.PENDING;
        this.updatedAt = LocalDateTime.now();
    }


}
