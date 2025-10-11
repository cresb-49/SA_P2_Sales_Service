package com.sap.sales_service.snacks.application.input;

import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;

public interface CreateSnackPort {
    Snack create(CreateSnackDTO createSnackDTO);
}
