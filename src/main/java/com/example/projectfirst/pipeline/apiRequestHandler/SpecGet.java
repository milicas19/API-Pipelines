package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.pipeline.model.Spec;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Value;
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
