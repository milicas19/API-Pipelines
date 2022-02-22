package com.example.projectfirst.pipeline.apiRequestHandler;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@JsonTypeName("API_POST")
public class SpecPost implements Spec {
    String url;
    String connectorID;
    String body;
    String output;
}
