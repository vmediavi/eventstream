package com.example.eventstream;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    public static final String TOPIC = "records";

    @Bean
    public ReactiveKafkaProducerTemplate<String, RecordEvent> reactiveKafkaProducer(KafkaProperties properties) {

        Map<String, Object> props = properties.buildProducerProperties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "host.docker.internal:9092");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new ReactiveKafkaProducerTemplate<String, RecordEvent>(SenderOptions.create(props));
    }

    @Bean
    public ReceiverOptions<String, RecordEvent> kafkaReceiver(KafkaProperties kafkaProperties) {

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "host.docker.internal:9092");

        config.put(ConsumerConfig.GROUP_ID_CONFIG, "reactive-kafka");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.TRUSTED_PACKAGES,"*");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE,"com.example.eventstream.RecordEvent");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        ReceiverOptions<String, RecordEvent> basicReceiverOptions = ReceiverOptions.create(config);
        return basicReceiverOptions.subscription(Collections.singletonList(TOPIC));

    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, RecordEvent> reactiveKafkaConsumer(ReceiverOptions<String, RecordEvent> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<String, RecordEvent>(kafkaReceiverOptions);
    }



//    @Bean
//    public Flux<RecordEvent> consume(ObjectMapper objectMapper) {
//        Map<String, Object> props = new HashMap<>();
//        props.put("bootstrap.servers", "localhost:29092");
//        props.put("group.id", "record-group");
//        props.put("key.deserializer", StringDeserializer.class);
//        props.put("value.deserializer", StringDeserializer.class);
//        props.put("auto.offset.reset", "earliest");
//
//        ReceiverOptions<String, String> options = ReceiverOptions.<String, String>create(props)
//                .subscription(Collections.singleton("records"));
//
//        return KafkaReceiver.create(options)
//                .receive()
//                .map(record -> {
//                    try {
//                        return objectMapper.readValue(record.value(), RecordEvent.class);
//                    } catch (Exception e) {
//                        throw new RuntimeException("Invalid JSON: " + record.value(), e);
//                    }
//                })
//                .share();
//    }
}