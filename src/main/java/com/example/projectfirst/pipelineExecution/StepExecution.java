package com.example.projectfirst.pipelineExecution;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StepExecution {
    Integer code;
    String msg;
    String output;
}
