package com.example.projectfirst.pipeline.model;

import com.example.projectfirst.pipeline.apiRequestHandler.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class StepParameters {
    String name;
    String type;
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SpecGet.class, name = "API_GET"),
            @JsonSubTypes.Type(value = SpecPost.class, name = "API_POST")
    })
    Spec spec;
    int retry;
    long backOffPeriod;
}
