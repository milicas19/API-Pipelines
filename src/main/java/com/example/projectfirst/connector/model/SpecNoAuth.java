package com.example.projectfirst.connector.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonTypeName("NO_AUTH")
public class SpecNoAuth implements Spec{
    String host;
}
