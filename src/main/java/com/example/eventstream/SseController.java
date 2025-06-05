package com.example.eventstream;

import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SseController {

    private final ReactiveKafkaConsumerTemplate<String, RecordEvent> reactiveKafkaConsumer;

    public SseController(ReactiveKafkaConsumerTemplate<String, RecordEvent> reactiveKafkaConsumer) {
        this.reactiveKafkaConsumer = reactiveKafkaConsumer;
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Record>> stream() {
        return  Flux.just(ServerSentEvent.<Record>builder().event("new").data(new Record("1","first record")).build(),
                ServerSentEvent.<Record>builder().event("new").data(new Record("2","second record")).build(),
                ServerSentEvent.<Record>builder().event("new").data(new Record("3","third record")).build(),
                ServerSentEvent.<Record>builder().event("new").data(new Record("4","fourth record")).build()
                )
                .mergeWith(
                        reactiveKafkaConsumer
                                .receiveAutoAck()
                                .map(evt ->

                    ServerSentEvent.<Record>builder()
                            .event(evt.value().type())
                            .data(evt.value().payload())
                            .build()));
    }
}
