package com.example.projectfirst.pipeline;

import lombok.*;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@NonFinal
@Jacksonized
@SuperBuilder
public class Spec {
    String url;
    String connectorID;
}
