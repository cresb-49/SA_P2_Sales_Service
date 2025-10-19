package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.filter.SaleFilter;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SaleMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleEntityRepository;
import com.sap.sales_service.sale.infrastructure.output.jpa.specifications.SaleEntitySpecs;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleJpaAdapter implements FindSalePort, SaveSalePort {

    private final SaleEntityRepository saleEntityRepository;

    private final SaleMapper saleMapper;

    @Override
    public Optional<Sale> findById(UUID id) {
        return saleEntityRepository.findById(id).map(saleMapper::toDomain);
    }

    @Override
    public List<Sale> findByCustomerId(UUID customerId) {
        var entities = saleEntityRepository.findByClientId(customerId);
        return entities.stream().map(saleMapper::toDomain).toList();
    }

    @Override
    public Page<Sale> findByCustomerIdPaginated(UUID customerId, int page) {
        var result = saleEntityRepository.findByClientId(customerId, PageRequest.of(page, 20));
        return result.map(saleMapper::toDomain);
    }

    @Override
    public Page<Sale> search(SaleFilter filter, int page) {
        var spec = SaleEntitySpecs.byFilter(filter);
        var result = saleEntityRepository.findAll(spec, PageRequest.of(page, 20));
        return result.map(saleMapper::toDomain);
    }

    @Override
    public Sale save(Sale sale) {
        var entity = saleMapper.toEntity(sale);
        var savedEntity = saleEntityRepository.save(entity);
        return saleMapper.toDomain(savedEntity);
    }
}
