package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;

import java.util.List;
import java.util.UUID;

public interface TicketMovieReportPort {
    List<MovieSummaryView> findMoviesByIds(List<UUID> movieIds);
}
