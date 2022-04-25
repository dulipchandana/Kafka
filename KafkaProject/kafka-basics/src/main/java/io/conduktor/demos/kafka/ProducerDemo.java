package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger log  = LoggerFactory
            .getLogger(ProducerDemo.class.getName());
    public static void main(String[] args) {
        log.info("start kafka producer");
        //create producer Property
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        //create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        //create a producer record
        ProducerRecord<String,String> producerRecord =
                new ProducerRecord<>("first_topic","from java code");

        //send data - asynchronous
        producer.send(producerRecord);
        // flush and close the producer - asynchronous
        producer.flush();

        //close producer
        producer.flush();

    }


}