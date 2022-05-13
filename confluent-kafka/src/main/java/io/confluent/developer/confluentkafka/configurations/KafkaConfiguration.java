package io.confluent.developer.confluentkafka.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    final static String TOPIC_NAME = "streams-count-output";

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name(TOPIC_NAME).partitions(6).replicas(3).build();
    }
}
