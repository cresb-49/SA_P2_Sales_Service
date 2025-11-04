package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.topcinemasales.dto.TopCinemaSalesReportDTO;

import java.time.LocalDate;

public interface TopCinemaSalesReportCasePort {
    TopCinemaSalesReportDTO report(LocalDate from, LocalDate to, int limit);
}
