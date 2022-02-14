package com.example.projectfirst.pipeline.model;

import com.example.projectfirst.pipeline.apiRequestHandler.Spec;
import com.example.projectfirst.pipeline.apiRequestHandler.SpecGet;
import com.example.projectfirst.pipeline.apiRequestHandler.SpecPost;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public StepParameters(StepParameters stepParameters){
        this.name = stepParameters.name;
        this.type = stepParameters.type;
        this.spec = stepParameters.spec;
        this.retry = stepParameters.retry;
        this.backOffPeriod = stepParameters.backOffPeriod;
    }
}
