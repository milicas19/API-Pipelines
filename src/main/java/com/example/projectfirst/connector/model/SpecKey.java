package com.example.projectfirst.connector.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonTypeName("API_KEY")
public class SpecKey implements Spec {
    String host;
    String keyHeaderName;
    String key;
}
