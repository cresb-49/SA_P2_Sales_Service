package com.sap.sales_service.snacks.infrastructure.output.jpa.adapter;

import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.domain.SnackFilter;
import com.sap.sales_service.snacks.infrastructure.output.jpa.mapper.SnackMapper;
import com.sap.sales_service.snacks.infrastructure.output.jpa.repository.SnackEntityRepository;
import com.sap.sales_service.snacks.infrastructure.output.jpa.specifications.SnackEntitySpecs;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SnackJpaAdapter implements SaveSnackPort, FindingSnackPort {

    private final SnackEntityRepository snackEntityRepository;
    private final SnackMapper snackMapper;

    @Override
    public Snack save(Snack snack) {
        var entity = snackEntityRepository.save(snackMapper.toEntity(snack));
        return snackMapper.toDomain(entity);
    }

    @Override
    public Optional<Snack> findById(UUID id) {
        var entity = snackEntityRepository.findById(id);
        return entity.map(snackMapper::toDomain);
    }

    @Override
    public Optional<Snack> findLikeNameAndCinemaId(String name, UUID cinemaId) {
        return snackEntityRepository.findByNameIgnoreCaseAndCinemaId(name, cinemaId)
                .map(snackMapper::toDomain);
    }

    @Override
    public List<Snack> findByIds(List<UUID> ids) {
        var entities = snackEntityRepository.findAllById(ids);
        return entities.stream().map(snackMapper::toDomain).toList();
    }

    @Override
    public Page<Snack> searchByFilter(SnackFilter snackFilter, int page) {
        var spec = SnackEntitySpecs.byFilter(snackFilter);
        var result = snackEntityRepository.findAll(spec, PageRequest.of(page, 20));
        return result.map(snackMapper::toDomain);
    }
}
