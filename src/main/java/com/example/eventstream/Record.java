package com.example.eventstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;


public record Record(    @JsonProperty("id")String id,
                         @JsonProperty("name") String name) implements Serializable{

}

