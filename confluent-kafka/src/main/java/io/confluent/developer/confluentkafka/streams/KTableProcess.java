package io.confluent.developer.confluentkafka.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.stereotype.Component;

//@Component
//@EnableKafkaStreams
@Slf4j
// lesson related to https://developer.confluent.io/learn-kafka/kafka-streams/hands-on-ktable/
public class KTableProcess {

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        final String inputTopic = "reactive_queue";
        final String outputTopic = "stream_ktable_output";
        var integerSerde = Serdes.Integer();
        final Serde<String> stringSerde = Serdes.String();

        KTable<Integer,String> firstKTable = streamsBuilder.table(inputTopic,
                Materialized.<Integer,String, KeyValueStore<Bytes,byte[]>> as("ktable-store")
                        .withKeySerde(integerSerde)
                        .withValueSerde(stringSerde));
        firstKTable.filter((key,value) -> value.toLowerCase().contains(","))
                .mapValues(value -> value.substring(value.indexOf(",")+1))
                .toStream()
                .peek((key,value) -> System.out.println("katable peek key"+key+"value"+value))
                .to(outputTopic, Produced.with(integerSerde, stringSerde));

    }
}
