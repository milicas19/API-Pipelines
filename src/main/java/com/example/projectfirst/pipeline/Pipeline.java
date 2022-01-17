package com.example.projectfirst.pipeline;

import lombok.*;
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
