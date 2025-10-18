package com.sap.sales_service.snacks.application.ouput;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.domain.SnackFilter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindingSnackPort {
    Optional<Snack> findById(UUID id);

    Optional<Snack> findLikeNameAndCinemaId(String name, UUID cinemaId);

    List<Snack> findByIds(List<UUID> ids);

    Page<Snack> searchByFilter(SnackFilter snackFilter, int page);
}
