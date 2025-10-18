package com.sap.sales_service.snacks.application.input;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.domain.SnackFilter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FindSnackPort {
    Snack findById(UUID id);

    Page<Snack> search(SnackFilter snackFilter, int page);

    List<Snack> findByIds(List<UUID> ids);
}
