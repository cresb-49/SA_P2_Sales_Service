package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TopCinemaSalesReportFactory {

    private final FindCinemaPort findCinemaPort;

    public CinemaSalesSummaryDTO withCinema(CinemaSalesSummaryDTO dto) {
        var cinema = findCinemaPort.findById(dto.cinemaId());
        return new CinemaSalesSummaryDTO(
                dto.cinemaId(),
                dto.totalAmount(),
                dto.totalSales(),
                cinema
        );
    }

    public List<CinemaSalesSummaryDTO> withCinema(List<CinemaSalesSummaryDTO> dtos) {
        return dtos.stream()
                .map(this::withCinema)
                .toList();
    }
}
