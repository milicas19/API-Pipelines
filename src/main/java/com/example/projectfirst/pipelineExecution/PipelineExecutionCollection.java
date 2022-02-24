package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipeline.model.StepParameters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineExecutionCollection {
    @Id
    private String id;
    private String pipelineId;
    private LocalDateTime startDate;
    private String state;
    private List<StepParameters> steps;
    private HashMap<String, String> output;
    private Integer numberOfExecutedSteps;

    public PipelineExecutionCollection(String pipelineId,
                                       LocalDateTime startDate,
                                       String state,
                                       List<StepParameters> steps,
                                       HashMap<String, String> output,
                                       Integer numberOfExecutedSteps) {
        this.pipelineId = pipelineId;
        this.startDate = startDate;
        this.state = state;
        this.steps = steps;
        this.output = output;
        this.numberOfExecutedSteps = numberOfExecutedSteps;
    }
}
