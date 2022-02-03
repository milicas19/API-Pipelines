package com.example.projectfirst.connector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
            @JsonSubTypes.Type(value = SpecNamePass.class, name = "NAME_PASS")
    })
    Spec spec;
}
