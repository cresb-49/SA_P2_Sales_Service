package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.snackreportbycinema.dto.SnackReportByCinemaReportDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface SnackReportByCinemaCasePort {
    SnackReportByCinemaReportDTO report(LocalDate from, LocalDate to, UUID cinemaId);

    byte[] generateReportFile(LocalDate from, LocalDate to, UUID cinemaId);
}
