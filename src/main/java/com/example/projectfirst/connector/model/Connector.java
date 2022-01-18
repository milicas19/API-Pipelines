package com.example.projectfirst.connector.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@SuperBuilder
public class Connector {
    String id;
    String name;
    String type;
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Spec.class, name = "NO_AUTH"),
            @JsonSubTypes.Type(value = SpecKey.class, name = "API_KEY"),
            @JsonSubTypes.Type(value = SpecUser.class, name = "API_USER"),
            @JsonSubTypes.Type(value = SpecKeyUser.class, name = "API_KEY_USER")
    })
    Spec spec;
}
