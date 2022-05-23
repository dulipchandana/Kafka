package io.confluent.developer.confluentkafka;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootApplication
public class ConfluentKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfluentKafkaApplication.class, args);
	}
}

@RequiredArgsConstructor
@Component
@Slf4j
class Producer {

 private final KafkaTemplate<Integer, String> template;

 Faker faker;

 @EventListener(ApplicationStartedEvent.class)
 public  void generate(){
     faker = Faker.instance();
     final Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));

     final Flux<String> quotes =  Flux.fromStream
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
                     return template.send("reactive_queue", faker.random().nextInt(42),it.getT2());
                 }
             }).blockLast();
 }
}