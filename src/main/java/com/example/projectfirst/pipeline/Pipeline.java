package com.example.projectfirst.pipeline;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Jacksonized
@SuperBuilder
public class Pipeline {
    String id;
    String name;
    String description;
    List<Step> steps;
}
