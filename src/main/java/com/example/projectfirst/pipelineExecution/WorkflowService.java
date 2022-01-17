package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipeline.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

    public String initiateExecution(String id) {
        String yaml = pipelineService.fetchPipeline(id).getYmlFile();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                    new TypeReference<>(){});
            Pipeline pipeline = pipelineMap.get("pipeline");
            PipelineExecutionCollection pipelineExecution = new PipelineExecutionCollection(LocalDateTime.now(),
                    "prepared", pipeline.getSteps(), new HashMap<>(), 0);
            pipelineExecutionRepository.save(pipelineExecution);
            return pipelineExecution.getId();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public String executePipeline(String pipelineExeId){
        Optional<PipelineExecutionCollection> pipelineExecution
                = pipelineExecutionRepository.findById(pipelineExeId);

        Integer numberOfExecutedSteps = pipelineExecution.get().getNumberOfExecutedSteps();
        for(StepParameters stepParameters: pipelineExecution.get().getSteps()) {

            if(numberOfExecutedSteps != 0){
                numberOfExecutedSteps -= 1;
                continue;
            }

            //expressionResolverService.resolveStep(pipelineExeId, stepParameters);
            Pair<String, String> pair = executionService.executeStep(pipelineExeId, stepParameters);
            String executionState = pair.getFirst();
            String response = pair.getSecond();
            if(executionState.equals("abort") || executionState.equals("paused"))
                return "Finished pipeline execution with status: " + executionState + "!";
            else
                if(!executionState.equals("running")) {
                    checkStateService.setState(pipelineExeId, "abort");
                    return executionState;
                }

            saveOutputService.save(pipelineExeId, response, stepParameters.getName());

            String currentState = checkStateService.checkState(pipelineExeId);
            if(!currentState.equals("running"))
                return "Finished pipeline execution with status: " + currentState + "!";
        }

        checkStateService.setState(pipelineExeId, "finished");
        return "Pipeline successfully executed!";
    }


}
