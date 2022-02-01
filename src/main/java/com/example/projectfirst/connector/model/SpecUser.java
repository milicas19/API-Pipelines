package com.example.projectfirst.connector.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@JsonTypeName("API_USER")
public class SpecUser implements Spec {
    String host;
    String userHeaderName;
    String username;
    String password;
}
