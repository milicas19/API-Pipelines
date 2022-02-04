package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipeline.model.StepParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExpressionResolverService {
    public void resolveStep(String pipelineExeId, StepParameters stepParameters) {
        log.info("Resolving expressions for " + stepParameters.getName()+ "!");
    }
}
