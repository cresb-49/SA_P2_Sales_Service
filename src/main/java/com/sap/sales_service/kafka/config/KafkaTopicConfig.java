package com.sap.sales_service.kafka.config;


import com.sap.common_lib.events.topics.TopicConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class KafkaTopicConfig {
    @Bean
    NewTopic addsCreated() {
        return new NewTopic(TopicConstants.UPDATE_PAID_STATUS_ADD_TOPIC, 1, (short) 1);
    }
}