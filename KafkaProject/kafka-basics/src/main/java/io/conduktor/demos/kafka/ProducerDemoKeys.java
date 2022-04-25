package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerDemoKeys {

    private static final Logger log  = LoggerFactory
            .getLogger(ProducerDemoKeys.class.getName());
    private static String topic = "first_topic";
    public static void main(String[] args) {
        log.info("start kafka producer - with callback");
        //create producer Property
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        //create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);
        //send data - synchronous
        int [] numbers = {1,2,3,4,5,6,7,8};
        IntStream.range(0, numbers.length)
                        .forEach(index -> {
                            sendMessage(producer,index) ;
                                }
                        );
    }

    private static void sendMessage(KafkaProducer<String,String> producer,int index){

        //create a producer record
        ProducerRecord<String,String> producerRecord =
                new ProducerRecord<>(topic,"id_"+index ,"message value "+index);

        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                // execute every time record is successfull or not
                if (exception == null){
                    log.info("Receive new metadata/ \n " +
                                    "Topics {} / \n" +
                                    "key {} / \n" +
                                    "Partition {} / \n" +
                                    "Offset {} / \n" +
                                    "Time Stamp {} / \n",metadata.topic(),producerRecord.key(),
                            metadata.partition(),metadata.offset(),
                            metadata.timestamp());
                } else {
                    log.error("Error while producing {}", exception.fillInStackTrace());
                }

            }
        });
        // flush and close the producer - asynchronous
        producer.flush();

        //close producer
        producer.flush();
    }


}
