package com.sap.sales_service.snacks.infrastructure.input.service;

import com.sap.sales_service.snacks.infrastructure.input.service.dtos.SnackInternalView;
import com.sap.sales_service.snacks.infrastructure.input.service.mappers.SnackInternalViewMapper;
import com.sap.sales_service.snacks.infrastructure.input.service.port.SnackGatewayPort;
import com.sap.sales_service.snacks.infrastructure.output.jpa.adapter.SnackJpaAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class SnackGateway implements SnackGatewayPort {

    private final SnackJpaAdapter snackJpaAdapter;
    private final SnackInternalViewMapper snackInternalViewMapper;

    @Override
    public Optional<SnackInternalView> findById(UUID id) {
        var domain = snackJpaAdapter.findById(id);
        return domain.map(snackInternalViewMapper::toView);
    }

    @Override
    public List<SnackInternalView> findByIds(List<String> ids) {
        var domains = snackJpaAdapter.findByIds(ids);
        return snackInternalViewMapper.toListView(domains);
    }
}
