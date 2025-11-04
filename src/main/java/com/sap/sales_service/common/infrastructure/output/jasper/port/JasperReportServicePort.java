package com.sap.sales_service.common.infrastructure.output.jasper.port;

import java.util.Collection;
import java.util.Map;

public interface JasperReportServicePort {

    byte[] toPdf(String template, Collection<?> data, Map<String, Object> params);
}
