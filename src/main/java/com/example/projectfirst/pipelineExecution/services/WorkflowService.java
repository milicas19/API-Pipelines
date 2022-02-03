package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipeline.PipelineService;
import com.example.projectfirst.pipeline.model.Pipeline;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionFailedException;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
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
    private StateService stateService;


    public String initiateExecution(String id) throws IOException {

        String yaml = pipelineService.fetchPipeline(id).getYmlFile();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                new TypeReference<>() {
                });
        Pipeline pipeline = pipelineMap.get("pipeline");
        PipelineExecutionCollection pipelineExecution = new PipelineExecutionCollection(pipeline.getId(), LocalDateTime.now(),
                "prepared", pipeline.getSteps(), new HashMap<>(), 0);
        pipelineExecutionRepository.save(pipelineExecution);

        return pipelineExecution.getId();
    }

    public PipelineExecutionCollection executePipeline(String pipelineExeId) {
        return pipelineExecutionRepository.findById(pipelineExeId)
                .map(pipelineExecution -> {
                    int numberOfExecutedSteps = pipelineExecution.getNumberOfExecutedSteps();

                    // executions step by step
                    for (StepParameters stepParameters : pipelineExecution.getSteps()) {

                        if (numberOfExecutedSteps != 0) {
                            numberOfExecutedSteps -= 1;
                            continue;
                        }

                        // check if previous step was unsuccessful
                        String state = stateService.checkState(pipelineExeId);
                        if (!(state.equals("prepared") || state.equals("running"))) {
                            throw new PipelineExecutionFailedException("Pipeline execution " + state + "!");
                        }

                        // expressionResolverService.resolveStep(pipelineExeId, stepParameters);

                        // set state = running and execute step
                        stateService.setState(pipelineExeId, "running");

                        //execution of step with retry mechanism
                        int maxNumOfRetry = (stepParameters.getRetry() == 0) ? 1 : stepParameters.getRetry();

                        RetryTemplate retryTemplate = new RetryTemplate();

                        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
                        fixedBackOffPolicy.setBackOffPeriod(stepParameters.getBackOffPeriod());

                        Map<Class<? extends Throwable>, Boolean> retryOnException = new HashMap<>();
                        retryOnException.put(IOException.class, true);
                        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxNumOfRetry, retryOnException);

                        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
                        retryTemplate.setRetryPolicy(retryPolicy);

                        try {
                            retryTemplate.execute(arg0 -> {
                                executionService.executeStep(pipelineExeId, stepParameters);
                                return null;
                            });
                        } catch (IOException e) {
                            stateService.setState(pipelineExeId, "aborted");
                            throw new PipelineExecutionFailedException("Pipeline execution failed!");
                        }
                    }

                    // successfully finished execution of pipeline
                    return stateService.setState(pipelineExeId, "finished");
                })
                .orElseThrow(() -> new PipelineExecutionNotFoundException(pipelineExeId));
    }
}
