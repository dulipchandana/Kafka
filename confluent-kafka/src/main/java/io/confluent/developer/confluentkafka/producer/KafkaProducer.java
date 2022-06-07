package io.confluent.developer.confluentkafka.producer;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<Integer, String> template;

    Faker faker;

    @EventListener(ApplicationStartedEvent.class)
    public void generate() {
        faker = Faker.instance();
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));

        final Flux<String> quotes = Flux.fromStream
                (Stream.generate(new Supplier<String>() {
                    @Override
                    public String get() {
                        return faker.hobbit().quote();
                    }
                }));

        Flux.zip(interval, quotes)
                .map(new Function<Tuple2<Long, String>, Object>() {
                    @Override
                    public Object apply(Tuple2<Long, String> it) {
                        //log.info("message = {}" ,it.getT2());
                        return sendMessage("reactive_queue", faker.random().nextInt(42), it.getT2());
                    }
                }).blockLast();
    }

    public ListenableFuture<SendResult<Integer, String>> sendMessage(String topic,
                                                                     Integer key, String message) {
        return template.send(topic,key,message);
    }
}
