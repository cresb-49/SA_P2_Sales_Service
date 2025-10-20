package com.sap.sales_service.kafka.config;


import com.sap.common_lib.events.topics.TopicConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class KafkaTopicConfig {
    @Bean
    NewTopic requestTicketTopic() {
        return new NewTopic(TopicConstants.REQUEST_TICKET_SALES_TOPIC, 1, (short) 1);
    }

    @Bean
    NewTopic responseTicketTopic() {
        return new NewTopic(TopicConstants.RESPONSE_TICKET_SALES_TOPIC, 1, (short) 1);
    }

    @Bean
    NewTopic updatePaidStatusAddTopic() {
        return new NewTopic(TopicConstants.UPDATE_PAID_STATUS_SALE_TOPIC, 1, (short) 1);
    }

    @Bean
    NewTopic salesPendingPaymentTopic() {
        return new NewTopic(TopicConstants.SALES_PENDING_PAYMENT_TOPIC, 1, (short) 1);
    }

    @Bean
    NewTopic refundAmountSaleTopic() {
        return new NewTopic(TopicConstants.REFUND_AMOUNT_SALE_TOPIC, 1, (short) 1);
    }
}