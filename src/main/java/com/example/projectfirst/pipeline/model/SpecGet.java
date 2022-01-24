package com.example.projectfirst.pipeline.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonTypeName("API_GET")
public class SpecGet implements Spec {
    String url;
    String connectorID;
    String output;
}
