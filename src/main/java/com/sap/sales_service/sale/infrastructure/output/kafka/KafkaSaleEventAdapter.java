package com.sap.sales_service.sale.infrastructure.output.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaSaleEventAdapter {
    private final KafkaTemplate<String, Object> kafkaTemplate;
}
