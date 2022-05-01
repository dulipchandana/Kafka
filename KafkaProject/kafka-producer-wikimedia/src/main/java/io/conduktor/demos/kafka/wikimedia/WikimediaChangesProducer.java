package io.conduktor.demos.kafka.wikimedia;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * ./kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic wikimedia.recentchanges --partitions 3 --replication-factor 1
 */
public class WikimediaChangesProducer {

    private static final Logger log  = LoggerFactory
            .getLogger(WikimediaChangesProducer.class.getName());
    public static void main(String[] args) throws InterruptedException {
        String bootstrapServer = "127.0.0.1:9092";

        log.info("start kafka producer - with callback");
        //create producer Property
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        // set safe  producer this is only for kafka producer =< 2.8
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));

        //set high throughput producer config
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024));
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");

        //create the producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);
        String topic  = "wikimedia.recentchanges";

        EventHandler eventHandler = new WikimediaChangeHandler(producer,topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();

        //start the producer
        eventSource.start();

        // produce for 10 min
        TimeUnit.MINUTES.sleep(10);
    }
}
