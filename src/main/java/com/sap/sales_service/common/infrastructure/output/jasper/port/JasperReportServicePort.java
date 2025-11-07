package com.sap.sales_service.common.infrastructure.output.jasper.port;

import java.util.Collection;
import java.util.Map;

public interface JasperReportServicePort {

    byte[] toPdfCompiled(String template, Collection<? extends Map<String, ?>> data, Map<String, Object> params);

    byte[] toPdfNonCompiled(String template, Collection<? extends Map<String, ?>> data, Map<String, Object> params);

    default byte[] toPdf(String template, Collection<? extends Map<String, ?>> data, Map<String, Object> params) {
        return toPdfCompiled(template, data, params);
    }
}
