package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.exceptions.APIPYamlParsingException;
import com.example.projectfirst.pipeline.PipelineService;
import com.example.projectfirst.pipeline.model.Pipeline;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.StatusOfStepExecution;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.exceptions.APIPInitiateExecutionFailed;
import com.example.projectfirst.exceptions.APIPPipelineExecutionFailedException;
import com.example.projectfirst.exceptions.APIPPipelineExecutionNotFoundException;
import com.example.projectfirst.exceptions.APIPRetryMechanismException;
import com.example.projectfirst.exceptions.APIPStepExecutionFailedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@AllArgsConstructor
@Slf4j
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
    @Autowired
    private SaveOutputService saveOutputService;

    public String initiateExecution(String pipelineId) throws APIPInitiateExecutionFailed {

        log.info("Initiation of pipeline execution begins!");
        String yaml = pipelineService.fetchPipeline(pipelineId).getYmlFile();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml, new TypeReference<>() {});
            Pipeline pipeline = pipelineMap.get("pipeline");
            PipelineExecutionCollection pipelineExecution = new PipelineExecutionCollection(pipeline.getId(),
                    LocalDateTime.now(),"prepared", pipeline.getSteps(), new HashMap<>(), 0);

            PipelineExecutionCollection execution = pipelineExecutionRepository.save(pipelineExecution);
            log.info("Initiation of pipeline execution successful!");
            return execution.getId();
        }catch (IOException e){
            throw new APIPInitiateExecutionFailed(e);
        }
    }

    public void executePipelineSteps(String pipelineExeId) {
        Optional<PipelineExecutionCollection> pipelineExecutionOptional
                = pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecutionOptional.isEmpty()) {
            log.error("Pipeline execution with id " + pipelineExeId + "not found!");
            throw new APIPPipelineExecutionNotFoundException("Could not find pipeline execution with id "
                    + pipelineExeId + "!");
        }
        PipelineExecutionCollection pipelineExecution = pipelineExecutionOptional.get();

        int numberOfExecutedSteps = pipelineExecution.getNumberOfExecutedSteps();
        // if pipeline execution is paused --> resume it
        if(numberOfExecutedSteps > 0){
            stateService.setState(pipelineExeId, "running");
        }
        int stepNum = numberOfExecutedSteps + 1;
        log.info("Executing pipeline from step" + stepNum + "!");

        // pipeline execution step by step
        for (StepParameters stepParameters : pipelineExecution.getSteps()) {

            if (numberOfExecutedSteps != 0) {
                numberOfExecutedSteps -= 1;
                continue;
            }

            String state = stateService.checkState(pipelineExeId);
            if (!(state.equals("prepared") || state.equals("running"))) {
                log.info("Pipeline is aborted or paused!");
                throw new APIPPipelineExecutionFailedException("Pipeline execution " + state + "!");
            }

            stateService.setState(pipelineExeId, "running");

            try {
            HashMap<String, String> pipelineExecutionOutput
                    = expressionResolverService.getPipelineExecutionOutput(pipelineExeId);

            StepParameters stepParametersResolvedBeforeExecution
                    = expressionResolverService.resolveStep(pipelineExecutionOutput, stepParameters, true);

            RetryTemplate retryTemplate = prepareRetryTemplate(stepParameters);

            retryTemplate.execute(arg0 -> {
                StepExecution stepExecution
                            = executionService.executeStep(stepParametersResolvedBeforeExecution);

                if(stepExecution.getStatus().equals(StatusOfStepExecution.SUCCESS)){
                    pipelineExecutionOutput.put(stepParametersResolvedBeforeExecution.getName(), stepExecution.getOutput());
                    StepParameters stepParametersAfterExecution
                            = expressionResolverService.resolveStep(
                                    pipelineExecutionOutput, stepParametersResolvedBeforeExecution,false);
                    String stepOutputAfter = stepParametersAfterExecution.getSpec().getOutput();
                    String stepOutputAtTheBeginning = stepParameters.getSpec().getOutput();

                    log.info("step output at the beginning: " + stepOutputAtTheBeginning);
                    log.info("step output after resolving: " + stepOutputAfter);

                    if(stepOutputAfter == null) {
                         log.info("their is no expression in output of " + stepParametersAfterExecution.getName());
                         saveOutputService.save(pipelineExeId, stepExecution.getOutput(),
                                 stepParametersAfterExecution.getName());
                    } else {
                        if (stepOutputAfter.equals(stepOutputAtTheBeginning)) {
                            log.info("unresolved expression in output");
                            stateService.setState(pipelineExeId, "failed");
                            throw new APIPPipelineExecutionFailedException("Pipeline execution failed: "
                                    + stepParameters.getName() + " failed! Unresolved expression in output!");
                        } else {
                            log.info("expression in output of " + stepParametersAfterExecution.getName());
                            saveOutputService.save(pipelineExeId, stepOutputAfter,
                                    stepParametersAfterExecution.getName());
                        }
                    }
                }else{
                    stateService.setState(pipelineExeId, "failed");
                    throw new APIPPipelineExecutionFailedException("Pipeline execution failed: "
                            + stepParameters.getName() + " failed!");
                }
                return null; });
            } catch (APIPStepExecutionFailedException e) {
                stateService.setState(pipelineExeId, "failed");
                throw new APIPPipelineExecutionFailedException("Pipeline execution failed: " +
                        stepParameters.getName() + " failed! Message" + e.getMessage());
            } catch (APIPYamlParsingException ex){
                stateService.setState(pipelineExeId, "failed");
                throw new APIPPipelineExecutionFailedException("Pipeline execution failed! " +
                        "Message" + ex.getMessage());
            } catch (IOException exx){
                stateService.setState(pipelineExeId, "failed");
                throw new APIPRetryMechanismException("Retry mechanism failed!");
            }
        }

        log.info("Pipeline successfully executed!");
        stateService.setState(pipelineExeId, "finished");
    }

    public RetryTemplate prepareRetryTemplate(StepParameters stepParameters){

        log.info("Preparing retry template!");

        int maxNumOfRetry = (stepParameters.getRetry() == 0) ? 1 : stepParameters.getRetry();

        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(stepParameters.getBackOffPeriod());

        Map<Class<? extends Throwable>, Boolean> retryOnException = new HashMap<>();
        retryOnException.put(APIPStepExecutionFailedException.class, true);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxNumOfRetry, retryOnException);

        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        log.info("Retry template prepared!");
        return retryTemplate;
    }
}
