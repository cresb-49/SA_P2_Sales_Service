package com.sap.sales_service.sale.application.usecases.snackreportbycinema;

import com.sap.sales_service.sale.application.factory.SnackReportByCinemaFactory;
import com.sap.sales_service.sale.application.input.SnackReportByCinemaCasePort;
import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.application.ouput.SnackReportByCinema;
import com.sap.sales_service.sale.application.usecases.snackreportbycinema.dto.SnackReportByCinemaReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SnackReportByCinemaCase implements SnackReportByCinemaCasePort {

    private final SnackReportByCinemaFactory snackReportByCinemaFactory;
    private final SnackReportByCinema snackReportByCinema;
    private final FindCinemaPort findCinemaPort;

    @Override
    public SnackReportByCinemaReportDTO report(LocalDate from, LocalDate to, UUID cinemaId) {
        // Get Information to from init day, to end day
        var result = snackReportByCinema.getSnackSalesByCinemaDTOs(
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                cinemaId
        );
        var resultWithSnackView = snackReportByCinemaFactory.withSnackView(result);
        // find cinema
        var cinema = findCinemaPort.findById(cinemaId);
        // calculate total
        BigDecimal total = resultWithSnackView.stream()
                .map(SnackSalesByCinemaDTO::totalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SnackReportByCinemaReportDTO(
                resultWithSnackView,
                total,
                cinema,
                from,
                to
        );
    }

    @Override
    public byte[] generateReportFile(LocalDate from, LocalDate to, UUID cinemaId) {
        return new byte[0];
    }
}
