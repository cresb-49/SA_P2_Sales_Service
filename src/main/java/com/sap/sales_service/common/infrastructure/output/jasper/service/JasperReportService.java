package com.sap.sales_service.common.infrastructure.output.jasper.service;


import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JasperReportService implements JasperReportServicePort {

    private final Map<String, JasperReport> compiledCache = new ConcurrentHashMap<>();
    private final Map<String, JasperReport> nonCompiledCache = new ConcurrentHashMap<>();

    @Override
    public byte[] toPdfCompiled(String template, Collection<? extends Map<String, ?>> data, Map<String, Object> params) {
        var report = compiledCache.computeIfAbsent(template, this::loadCompiledReport);
        return exportToPdf(report, data, params);
    }

    @Override
    public byte[] toPdfNonCompiled(String template, Collection<? extends Map<String, ?>> data, Map<String, Object> params) {
        var report = nonCompiledCache.computeIfAbsent(template, this::compileReport);
        return exportToPdf(report, data, params);
    }

    @Override
    public byte[] toPdf(String template, Collection<? extends Map<String, ?>> data, Map<String, Object> params) {
        return toPdfCompiled(template, data, params);
    }

    private JasperReport loadCompiledReport(String template) {
        var path = "/reports/" + template + ".jasper";
        try (var in = openResource(path)) {
            return (JasperReport) JRLoader.loadObject(in);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar " + path + ",message: " + e.getMessage(), e);
        }
    }

    private JasperReport compileReport(String template) {
        var path = "/reports/" + template + ".jrxml";
        try (var in = openResource(path)) {
            return JasperCompileManager.compileReport(in);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo compilar " + template + ",message: " + e.getMessage(), e);
        }
    }

    private InputStream openResource(String path) {
        var in = getClass().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalStateException("No se encontró el recurso " + path);
        }
        return in;
    }

    private byte[] exportToPdf(JasperReport report, Collection<? extends Map<String, ?>> data, Map<String, Object> params) {
        try {
            var normalized = (data == null ? List.<Map<String, ?>>of() : data)
                    .stream()
                    .map(entry -> entry == null ? Map.<String, Object>of() : entry)
                    .toList();
            var ds = new JRMapCollectionDataSource(normalized);
            var jp = JasperFillManager.fillReport(report, params, ds);
            try (var out = new ByteArrayOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jp, out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF, message: " + e.getMessage(), e);
        }
    }
}
