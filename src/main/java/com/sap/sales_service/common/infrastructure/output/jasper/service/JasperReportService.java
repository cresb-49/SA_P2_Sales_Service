package com.sap.sales_service.common.infrastructure.output.jasper.service;


import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JasperReportService implements JasperReportServicePort {

    private final Map<String, JasperReport> cache = new ConcurrentHashMap<>();

    private JasperReport load(String template) {
        return cache.computeIfAbsent(template, t -> {
            try (var in = getClass().getResourceAsStream("/reports/" + t + ".jrxml")) {
                return JasperCompileManager.compileReport(in);
            } catch (Exception e) {
                throw new IllegalStateException("No se pudo compilar " + t, e);
            }
        });
    }

    @Override
    public byte[] toPdf(String template, Collection<?> data, Map<String, Object> params) {
        try {
            var jr = load(template);
            var ds = new JRBeanCollectionDataSource(data == null ? List.of() : data);
            var jp = JasperFillManager.fillReport(jr, params, ds);
            try (var out = new ByteArrayOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jp, out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}
