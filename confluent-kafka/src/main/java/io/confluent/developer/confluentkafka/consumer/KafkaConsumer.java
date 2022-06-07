package io.confluent.developer.confluentkafka.consumer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@Data
public class KafkaConsumer {

    private ConsumerRecord<Integer,String> record;

    @KafkaListener(topics = {"reactive_queue"}, groupId = "spring-boot-kafka")
    public void consume(ConsumerRecord<Integer,String> record) {
        log.info("received key {} message {}", record.key(),record.value());
        this.record = record;

    }
}
