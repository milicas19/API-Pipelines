package com.example.projectfirst.connector.model;

import com.example.projectfirst.connector.model.Spec;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("API_USER")
public class SpecUser extends Spec {
    String userHeaderName;
    String username;
    String password;
}
