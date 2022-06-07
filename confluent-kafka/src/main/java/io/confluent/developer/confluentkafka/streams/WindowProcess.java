package io.confluent.developer.confluentkafka.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
@EnableKafkaStreams
@Slf4j
/**
 * https://developer.confluent.io/learn-kafka/kafka-streams/hands-on-windowing/
 */
public class WindowProcess {
    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        final String inputTopic = "reactive_queue";
        final String outputTopic = "stream_window_output";
        final Serde<Integer> integerSerde = Serdes.Integer();
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        final KStream<Integer, String> messageStream = streamsBuilder
                .stream(inputTopic, Consumed.with(integerSerde,stringSerde))
                .peek((key,value) -> log.info("key -{} , value -{}",key,value));

        messageStream
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofMinutes(1)).grace(Duration.ofMinutes(1)))
                .aggregate(() -> 0L,
                        (key,value, total) -> total + value.length(),
                        Materialized.with(integerSerde,Serdes.Long()))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .map((wk,value) -> KeyValue.pair(wk.key(),value))
                .peek((key, value) -> log.info("after window key - {} , value {}", key, value))
                .to(outputTopic,Produced.with(integerSerde,longSerde));
    }
}
