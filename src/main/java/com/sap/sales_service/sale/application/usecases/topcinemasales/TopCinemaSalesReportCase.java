package com.sap.sales_service.sale.application.usecases.topcinemasales;

import com.sap.sales_service.sale.application.factory.TopCinemaSalesReportFactory;
import com.sap.sales_service.sale.application.input.TopCinemaSalesReportCasePort;
import com.sap.sales_service.sale.application.ouput.TopCinemaSalesReportPort;
import com.sap.sales_service.sale.application.usecases.topcinemasales.dto.TopCinemaSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TopCinemaSalesReportCase implements TopCinemaSalesReportCasePort {

    private final TopCinemaSalesReportPort topCinemaSalesReportPort;
    private final TopCinemaSalesReportFactory topCinemaSalesReportFactory;

    @Override
    public TopCinemaSalesReportDTO report(LocalDate from, LocalDate to, int limit) {
        int topLimit = limit <= 0 ? 5 : limit;
        List<CinemaSalesSummaryDTO> summaries = topCinemaSalesReportPort.getTopCinemaSales(
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                topLimit
        );
        var enriched = topCinemaSalesReportFactory.withCinema(summaries);
        return new TopCinemaSalesReportDTO(
                enriched,
                from,
                to
        );
    }
}
