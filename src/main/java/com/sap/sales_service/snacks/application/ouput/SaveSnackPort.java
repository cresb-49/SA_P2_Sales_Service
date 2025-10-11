package com.sap.sales_service.snacks.application.ouput;

import com.sap.sales_service.snacks.domain.Snack;

public interface SaveSnackPort {
    Snack save(Snack snack);
}
