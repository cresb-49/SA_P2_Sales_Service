package com.sap.sales_service.snacks.application.usecases.updatestatesnack;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.snacks.application.input.UpdateStateSnackPort;
import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateStateSnackCase implements UpdateStateSnackPort {

    private final FindingSnackPort findingSnackPort;
    private final SaveSnackPort saveSnackPort;

    @Override
    public void updateStateSnackById(UUID snackId) {
        var snack = findingSnackPort.findById(snackId)
                .orElseThrow(() -> new NotFoundException("Snack with id " + snackId + " does not exist"));
        snack.toggleActive();
        saveSnackPort.save(snack);
    }
}
