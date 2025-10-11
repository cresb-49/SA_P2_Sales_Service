package com.sap.sales_service.snacks.application.input;

import com.sap.sales_service.snacks.application.usecases.updatesnack.dtos.UpdateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;

public interface UpdateSnackPort {
    Snack update(UpdateSnackDTO updateSnackDTO);
}
