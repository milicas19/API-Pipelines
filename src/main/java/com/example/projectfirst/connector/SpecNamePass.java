package com.example.projectfirst.connector;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("NAME_PASS")
public class SpecNamePass extends Spec{
    String username;
    String password;
}
