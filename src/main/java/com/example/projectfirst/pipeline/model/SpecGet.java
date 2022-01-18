package com.example.projectfirst.pipeline.model;

import com.example.projectfirst.pipeline.model.Spec;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonTypeName("API_GET")
public class SpecGet implements Spec {
    String output;
    String url;
    String connectorID;
}
