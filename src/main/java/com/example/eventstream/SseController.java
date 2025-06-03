package com.example.eventstream;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SseController {

    private final Flux<RecordEvent> kafkaEvents;

    public SseController(Flux<RecordEvent> kafkaEvents) {
        this.kafkaEvents = kafkaEvents;
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Record>> stream() {
        return  Flux.just(ServerSentEvent.<Record>builder().event("new").data(new Record("1","first record")).build(),
                ServerSentEvent.<Record>builder().event("new").data(new Record("2","second record")).build(),
                ServerSentEvent.<Record>builder().event("new").data(new Record("3","third record")).build(),
                ServerSentEvent.<Record>builder().event("new").data(new Record("4","fourth record")).build()
                )
                .mergeWith(
                kafkaEvents.map(evt ->
                    ServerSentEvent.<Record>builder()
                            .event(evt.type())
                            .data(evt.payload())
                            .build()));
    }
}
