package io.confluent.developer.confluentkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaStreamConsumer {

    @KafkaListener(topics = {"streams-count-output"}, groupId = "spring-boot-kafka")
    public Long consume(ConsumerRecord<String,Long> record
                        //String quote
            ) {
        log.info("received messageId  = {} message {}", record.key() , record.value());
        return record.value();

    }
}
