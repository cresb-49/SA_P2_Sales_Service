package com.sap.sales_service.snacks.application.input;

import java.util.UUID;

public interface UpdateStateSnackPort {
    void updateStateSnackById(UUID snackId);
}
