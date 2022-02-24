package com.example.projectfirst.pipeline.apiRequestHandler;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@Jacksonized
@Builder
@JsonTypeName("API_GET")
public class SpecGet implements Spec {
    String url;
    String connectorID;
    String output;
}
