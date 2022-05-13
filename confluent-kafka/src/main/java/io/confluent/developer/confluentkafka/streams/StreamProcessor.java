package io.confluent.developer.confluentkafka.streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

/**
 * https://docs.confluent.io/platform/current/streams/javadocs/javadoc/org/apache/kafka/streams/StreamsBuilder.html?utm_source=youtube&utm_medium=video&utm_campaign=tm.devx_ch.cd-spring-framework-and-apache-kafka_content.frameworks
 */
@Component
@EnableKafkaStreams
public class StreamProcessor {

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {

        var integerSerde = Serdes.Integer();
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        KStream<Integer,String> textLines = streamsBuilder.stream("reactive_queue",
                Consumed.with(integerSerde,stringSerde));

        KTable<String,Long> wordCount = textLines
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value, Grouped.with(stringSerde,stringSerde))
                .count();
        wordCount.toStream().to("streams-count-output", Produced.with(stringSerde,longSerde));
    }
}
