package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.dtos.MovieView;

import java.util.UUID;

public interface FindingMoviePort {
    MovieView findMovieById(UUID movieId);
}
