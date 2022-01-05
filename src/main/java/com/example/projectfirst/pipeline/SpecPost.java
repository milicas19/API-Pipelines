package com.example.projectfirst.pipeline;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("API_POST")
public class SpecPost extends Spec{
    String value;
}
