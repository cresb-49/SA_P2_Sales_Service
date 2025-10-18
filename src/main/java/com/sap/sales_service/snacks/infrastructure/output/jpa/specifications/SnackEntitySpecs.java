package com.sap.sales_service.snacks.infrastructure.output.jpa.specifications;

import com.sap.sales_service.snacks.domain.SnackFilter;
import com.sap.sales_service.snacks.infrastructure.output.jpa.entity.SnackEntity;
import org.springframework.data.jpa.domain.Specification;

public class SnackEntitySpecs {
    public static Specification<SnackEntity> byFilter(SnackFilter f) {
        return Specification.allOf(
                eqName(f.name()),
                eqActive(f.active()),
                eqCinemaId(f.cinemaId())
        );
    }

    private static Specification<SnackEntity> eqName(String name) {
        return (root, q, cb) -> name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<SnackEntity> eqActive(Boolean active) {
        return (root, q, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    private static Specification<SnackEntity> eqCinemaId(java.util.UUID cinemaId) {
        return (root, q, cb) -> cinemaId == null ? null : cb.equal(root.get("cinemaId"), cinemaId);
    }
}
