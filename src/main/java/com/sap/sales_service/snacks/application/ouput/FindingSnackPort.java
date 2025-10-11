package com.sap.sales_service.snacks.application.ouput;

import com.sap.sales_service.snacks.domain.Snack;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindingSnackPort {
    Optional<Snack> findById(UUID id);
    Page<Snack> findAll(int page);
    Page<Snack> findLikeName(String name, int page);
    List<Snack> findByIds(List<String> ids);
}
