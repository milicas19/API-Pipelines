package com.example.projectfirst.pipelineExecution;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StepExecution {
    StatusOfStepExecution status;
    String output;
}
