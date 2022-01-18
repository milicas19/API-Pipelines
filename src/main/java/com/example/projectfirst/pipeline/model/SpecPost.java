package com.example.projectfirst.pipeline.model;

import com.example.projectfirst.pipeline.model.Spec;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonTypeName("API_POST")
public class SpecPost implements Spec {
    String body;
    String url;
    String connectorID;
}
