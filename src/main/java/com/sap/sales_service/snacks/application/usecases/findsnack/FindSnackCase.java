package com.sap.sales_service.snacks.application.usecases.findsnack;

import com.sap.sales_service.snacks.application.input.FindSnackPort;
import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.domain.Snack;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FindSnackCase implements FindSnackPort {

    private final FindingSnackPort findingSnackPort;

    @Override
    public Snack findById(UUID id) {
        return findingSnackPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Snack not found"));
    }

    @Override
    public Page<Snack> findAll(int page) {
        return findingSnackPort.findAll(page);
    }

    @Override
    public Page<Snack> findLikeName(String name, int page) {
        return findingSnackPort.findLikeName(name, page);
    }

    @Override
    public List<Snack> findByIds(List<String> ids) {
        return findingSnackPort.findByIds(ids);
    }
}
