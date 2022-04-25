package io.conduktor.demos.kafka.io.conduktor.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemo {

    private static final Logger log = LoggerFactory.getLogger
            (ConsumerDemo.class.getSimpleName());

    private static final String BOOTSTRP_SERVER = "127.0.0.1:9092";
    private static final String GROUP_ID = "second-application";
    private static final String TOPIC = "first_topic";

    public static void main(String[] args) {

        log.info("Kafka consumer");
        //create consumer config
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRP_SERVER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        /** options for AUTO_OFFSET_RESET_CONFIG
         * none/earliest/latest
         * */

        //create consumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(properties);

        // subscribe consumer to topics
        consumer.subscribe(Arrays.asList(TOPIC));

        //poll for new data

        while(true) {
            log.info("polling");
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000));

            records.forEach(record ->{
                    log.info("Key {} - value {}",record.key(),record.value());
                    log.info("Partition {} - Offset {}", record.partition(),record.offset());
            });

        }
    }
}
