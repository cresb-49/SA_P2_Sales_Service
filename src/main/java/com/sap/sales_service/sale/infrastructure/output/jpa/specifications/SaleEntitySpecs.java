package com.sap.sales_service.sale.infrastructure.output.jpa.specifications;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.sales_service.sale.domain.filter.SaleFilter;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SaleEntitySpecs {
    public static Specification<SaleEntity> byFilter(SaleFilter f) {
        return Specification.allOf(
                eqClientId(f.clientId()),
                eqCinemaId(f.cinemaId()),
                eqStatus(f.status()),
                leCreatedAt(f.maxCreatedAt()),
                geCreatedAt(f.minCreatedAt()),
                leUpdatedAt(f.maxUpdatedAt()),
                geUpdatedAt(f.minUpdatedAt()),
                lePaidAt(f.maxPaidAt()),
                gePaidAt(f.minPaidAt())

        );
    }

    private static Specification<SaleEntity> eqClientId(java.util.UUID clientId) {
        return (root, q, cb) -> clientId == null ? null : cb.equal(root.get("clientId"), clientId);
    }

    private static Specification<SaleEntity> eqCinemaId(java.util.UUID cinemaId) {
        return (root, q, cb) -> cinemaId == null ? null : cb.equal(root.get("cinemaId"), cinemaId);
    }

    private static Specification<SaleEntity> eqStatus(SaleStatusType status) {
        return (root, q, cb) -> status == null ? null : cb.equal(root.get("status"), status.name());
    }

    private static Specification<SaleEntity> leCreatedAt(LocalDateTime maxCreatedAt) {
        return (root, q, cb) -> maxCreatedAt == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), maxCreatedAt);
    }

    private static Specification<SaleEntity> geCreatedAt(LocalDateTime minCreatedAt) {
        return (root, q, cb) -> minCreatedAt == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), minCreatedAt);
    }

    private static Specification<SaleEntity> leUpdatedAt(LocalDateTime maxUpdatedAt) {
        return (root, q, cb) -> maxUpdatedAt == null ? null : cb.lessThanOrEqualTo(root.get("updatedAt"), maxUpdatedAt);
    }

    private static Specification<SaleEntity> geUpdatedAt(LocalDateTime minUpdatedAt) {
        return (root, q, cb) -> minUpdatedAt == null ? null : cb.greaterThanOrEqualTo(root.get("updatedAt"), minUpdatedAt);
    }

    private static Specification<SaleEntity> lePaidAt(LocalDateTime maxPaidAt) {
        return (root, q, cb) -> maxPaidAt == null ? null : cb.lessThanOrEqualTo(root.get("paidAt"), maxPaidAt);
    }

    private static Specification<SaleEntity> gePaidAt(LocalDateTime minPaidAt) {
        return (root, q, cb) -> minPaidAt == null ? null : cb.greaterThanOrEqualTo(root.get("paidAt"), minPaidAt);
    }
}
