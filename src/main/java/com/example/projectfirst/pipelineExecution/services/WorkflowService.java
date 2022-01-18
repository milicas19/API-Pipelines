package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipeline.*;
import com.example.projectfirst.pipeline.model.Pipeline;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkflowService {

    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Autowired
    private PipelineService pipelineService;
    @Autowired
    private ExpressionResolverService expressionResolverService;
    @Autowired
    private ExecutionService executionService;
    @Autowired
    private SaveOutputService saveOutputService;
    @Autowired
    private StateService checkStateService;

    @Retryable(value = {IOException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000,multiplier = 2))
    public String initiateExecution(String id) throws IOException {

        String yaml = pipelineService.fetchPipeline(id).getYmlFile();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                new TypeReference<>(){});
        Pipeline pipeline = pipelineMap.get("pipeline");
        PipelineExecutionCollection pipelineExecution = new PipelineExecutionCollection(LocalDateTime.now(),
                "prepared", pipeline.getSteps(), new HashMap<>(), 0);
        pipelineExecutionRepository.save(pipelineExecution);

        return pipelineExecution.getId();
    }

    public String executePipeline(String pipelineExeId){
        Optional<PipelineExecutionCollection> pipelineExecution
                = pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecution.isEmpty())
            throw new PipelineExecutionNotFoundException(pipelineExeId);

        int numberOfExecutedSteps = pipelineExecution.get().getNumberOfExecutedSteps();
        for(StepParameters stepParameters: pipelineExecution.get().getSteps()) {

            if(numberOfExecutedSteps != 0){
                numberOfExecutedSteps -= 1;
                continue;
            }

            //expressionResolverService.resolveStep(pipelineExeId, stepParameters);
            try {
                Pair<String, String> pair = executionService.executeStep(pipelineExeId, stepParameters);
                String executionState = pair.getFirst();
                String response = pair.getSecond();
                if (executionState.equals("aborted") || executionState.equals("paused"))
                    return "Finished pipeline execution with state: " + executionState + "!";
                saveOutputService.save(pipelineExeId, response, stepParameters.getName());
            }catch (IOException ex){
                checkStateService.setState(pipelineExeId, "aborted");
                return "Step " + stepParameters.getName() + " failed!";
            }

            String currentState = checkStateService.checkState(pipelineExeId);
            if(!currentState.equals("running"))
                return "Finished pipeline execution with status: " + currentState + "!";
        }

        checkStateService.setState(pipelineExeId, "finished");
        return "Pipeline successfully executed!";
    }


}
