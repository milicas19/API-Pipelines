package com.example.projectfirst.pipeline.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Value
@Jacksonized
@Builder
public class Pipeline {
    String id;
    String name;
    String description;
    List<StepParameters> steps;
}
