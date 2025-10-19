package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.FindSaleLineSnackPort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineSnackPort;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SaleLineSnackMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleLineSnackEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleLineSnackJpaAdapter implements FindSaleLineSnackPort, SaveSaleLineSnackPort {

    private final SaleLineSnackEntityRepository saleLineSnackEntityRepository;
    private final SaleLineSnackMapper saleLineSnackMapper;

    @Override
    public SaleLineSnack save(SaleLineSnack saleLineSnack) {
        var entity = saleLineSnackMapper.toEntity(saleLineSnack);
        var savedEntity = saleLineSnackEntityRepository.save(entity);
        return saleLineSnackMapper.toDomain(savedEntity);
    }

    @Override
    public List<SaleLineSnack> findAllBySaleId(UUID saleId) {
        return saleLineSnackEntityRepository.findAllBySaleId(saleId).stream()
                .map(saleLineSnackMapper::toDomain).toList();
    }

    @Override
    public Optional<SaleLineSnack> findById(UUID id) {
        return saleLineSnackEntityRepository.findById(id)
                .map(saleLineSnackMapper::toDomain);
    }
}
