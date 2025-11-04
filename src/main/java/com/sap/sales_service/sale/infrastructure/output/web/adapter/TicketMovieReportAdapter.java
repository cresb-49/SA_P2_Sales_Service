package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.MovieGatewayPort;
import com.sap.sales_service.sale.application.ouput.TicketMovieReportPort;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.MovieSummaryViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TicketMovieReportAdapter implements TicketMovieReportPort {

    private final MovieGatewayPort movieGatewayPort;
    private final MovieSummaryViewMapper movieSummaryViewMapper;

    @Override
    public List<MovieSummaryView> findMoviesByIds(List<UUID> movieIds) {
        if (movieIds == null || movieIds.isEmpty()) {
            return List.of();
        }
        var dtos = movieGatewayPort.getMoviesByIds(movieIds);
        return movieSummaryViewMapper.toViewList(dtos);
    }
}
