package com.sap.sales_service.kafka.config;

import com.sap.common_lib.exception.NonRetryableBusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
class KafkaErrorHandlingConfig {
    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
//        var recoverer = new DeadLetterPublishingRecoverer(template,
//                (cr, ex) -> new org.apache.kafka.common.TopicPartition(
//                        cr.topic() + ".DLQ", cr.partition())
//        );
//        var backoff = new ExponentialBackOff(500L, 2.0); // 0.5s, 1s, 2s…
//        backoff.setMaxElapsedTime(10_000L); // hasta ~10s
//        var handler = new DefaultErrorHandler(recoverer, backoff);
//        // Si quieres que ciertas excepciones no se reintenten se realizan asi, como un NotFoundException
//        // si no lo encuentras no tiene sentido reintentar
//        handler.addNotRetryableExceptions(NonRetryableBusinessException.class);
//        return handler;
        var recoverer = new DeadLetterPublishingRecoverer(
                template,
                (cr, ex) -> new org.apache.kafka.common.TopicPartition(cr.topic() + ".DLQ", cr.partition())
        );
        // Reintentos limitados (p. ej. 5 intentos cada 30s)
        var backoff = new FixedBackOff(30_000L, 5L);
        var handler = new DefaultErrorHandler(recoverer, backoff);
        handler.addNotRetryableExceptions(NonRetryableBusinessException.class);
        return handler;
    }
}