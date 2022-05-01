package io.conduktor.demos.kafka.wikimedia;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikimediaChangeHandler implements EventHandler {

    private KafkaProducer<String,String> kafkaProducer;
    private String topic;
    private static final Logger log  = LoggerFactory
            .getLogger(WikimediaChangeHandler.class.getName());

    public WikimediaChangeHandler(final KafkaProducer<String,String> kafkaProducer ,
                                  final String topic) {
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;

    }

    @Override
    public void onOpen() throws Exception {

    }

    @Override
    public void onClosed() throws Exception {
        kafkaProducer.close();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        log.info(messageEvent.getData());
        //asynchronous
        kafkaProducer.send(new ProducerRecord<>(topic,messageEvent.getData()));
    }

    @Override
    public void onComment(String comment) throws Exception {

    }

    @Override
    public void onError(Throwable t) {
        log.error("Error on receive message {}", t.getMessage());
    }
}
