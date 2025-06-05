package com.example.eventstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

public record RecordEvent(    @JsonProperty("type") String type,
                              @JsonProperty("payload") Record payload) implements Serializable {


}

