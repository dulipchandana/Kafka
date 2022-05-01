package io.conduktor.demos.kafka.openserch;

import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class OpenSearchConsumer {

    private static final Logger log  = LoggerFactory
            .getLogger(OpenSearchConsumer.class.getName());

    private static final String TOPIC = "wikimedia.recentchanges";
    public static void main(String[] args) {
        //create kafka client

        KafkaConsumer<String , String> kafkaConsumer = createKafkaConsumer();
        // consumer subscribe
        kafkaConsumer.subscribe(Collections.singleton(TOPIC));
        while(true){
            ConsumerRecords<String,String> records = kafkaConsumer.poll(Duration.ofMillis(3000));
            int recordCount = records.count();
            log.info("Record count {} of records",recordCount);
            records.forEach(record ->{
                String id = extractId(record.value());
                log.info("id {} - Data {}",id ,record.value());
            });
        }
    }

    private static String extractId(String json) {

        return JsonParser.parseString(json)
                .getAsJsonObject()
                .get("meta")
                .getAsJsonObject()
                .get("id")
                .getAsString();
    }

    private static KafkaConsumer<String,String> createKafkaConsumer(){
        final String BOOTSTRP_SERVER = "127.0.0.1:9092";
        final String GROUP_ID = "consumer-opensearch";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRP_SERVER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        /** options for AUTO_OFFSET_RESET_CONFIG
         * none/earliest/latest
         * */

        //create consumer
        return new KafkaConsumer<>(properties);
    }
}
