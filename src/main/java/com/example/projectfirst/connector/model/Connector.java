package com.example.projectfirst.connector.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class Connector {
    String id;
    String name;
    String type;
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SpecNoAuth.class, name = "NO_AUTH"),
            @JsonSubTypes.Type(value = SpecKey.class, name = "API_KEY"),
            @JsonSubTypes.Type(value = SpecUser.class, name = "API_USER"),
            @JsonSubTypes.Type(value = SpecKeyUser.class, name = "API_KEY_USER"),
            @JsonSubTypes.Type(value = SpecKeyToken.class, name = "API_KEY_TOKEN")
    })
    Spec spec;
}
