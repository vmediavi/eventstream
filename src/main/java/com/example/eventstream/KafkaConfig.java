package com.example.eventstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public Flux<RecordEvent> kafkaFlux(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "192.168.0.15:30000");
        props.put("group.id", "record-group");
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        props.put("auto.offset.reset", "earliest");

        ReceiverOptions<String, String> options = ReceiverOptions.<String, String>create(props)
                .subscription(Collections.singleton("records"));

        return KafkaReceiver.create(options)
                .receive()
                .map(record -> {
                    try {
                        return objectMapper.readValue(record.value(), RecordEvent.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid JSON: " + record.value(), e);
                    }
                })
                .share();
    }
}