package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.snacksalesreport.dto.SnackSalesReportDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface SnackSalesReportCasePort {
    SnackSalesReportDTO report(LocalDate from, LocalDate to, UUID cinemaId);
    byte[] generateReportFile(LocalDate from, LocalDate to, UUID cinemaId);
}
