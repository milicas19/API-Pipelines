package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;
import com.example.projectfirst.pipeline.PipelineService;
import com.example.projectfirst.pipeline.model.Pipeline;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPInitiateExecutionFailed;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionFailedException;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionNotFoundException;
import com.example.projectfirst.pipelineExecution.exception.APIPRetryMechanismException;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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


    public String initiateExecution(String id) throws APIPInitiateExecutionFailed {

        log.info("Initiation of pipeline execution begins!");
        String yaml = pipelineService.fetchPipeline(id).getYmlFile();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                    new TypeReference<>() {
                    });
            Pipeline pipeline = pipelineMap.get("pipeline");
            PipelineExecutionCollection pipelineExecution = new PipelineExecutionCollection(pipeline.getId(), LocalDateTime.now(),
                    "prepared", pipeline.getSteps(), new HashMap<>(), 0);
            pipelineExecutionRepository.save(pipelineExecution);
            log.info("Initiation of pipeline execution successful!");
            return pipelineExecution.getId();
        }catch (IOException e){
            throw new APIPInitiateExecutionFailed(e);
        }
    }

    public PipelineExecutionCollection executePipelineSteps(String pipelineExeId)
            throws APIPWrongYmlFileOfConnectorException, APIPRetryMechanismException {


        Optional<PipelineExecutionCollection> pipelineExecutionOp
                = pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecutionOp.isEmpty()) {
            log.error("Pipeline execution with id " + pipelineExeId + "not found!");
            throw new APIPPipelineExecutionNotFoundException(pipelineExeId);
        }
        PipelineExecutionCollection pipelineExecution = pipelineExecutionOp.get();

        int numberOfExecutedSteps = pipelineExecution.getNumberOfExecutedSteps();
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

            // expressionResolverService.resolveStep(pipelineExeId, stepParameters);

            int maxNumOfRetry = (stepParameters.getRetry() == 0) ? 1 : stepParameters.getRetry();

            RetryTemplate retryTemplate = new RetryTemplate();

            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(stepParameters.getBackOffPeriod());

            Map<Class<? extends Throwable>, Boolean> retryOnException = new HashMap<>();
            retryOnException.put(APIPStepExecutionFailedException.class, true);
            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxNumOfRetry, retryOnException);

            retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
            retryTemplate.setRetryPolicy(retryPolicy);

            try {
                retryTemplate.execute(arg0 -> {
                    stateService.setState(pipelineExeId, "running");
                    StepExecution stepExecution
                            = executionService.executeStep(pipelineExeId, stepParameters);
                    if(stepExecution.getCode()==200){
                        saveOutputService.save(pipelineExeId, stepExecution.getOutput(), stepParameters.getName());
                    }else{
                        log.error("Failed to execute step! Message: " + stepExecution.getMsg());
                        throw new APIPPipelineExecutionFailedException("Pipeline execution failed: " + stepParameters.getName() + " failed!");
                    }
                    return null;
                });
            } catch (APIPStepExecutionFailedException e) {
                log.error("Failed to execute step! Message: " + e.getMessage());
                stateService.setState(pipelineExeId, "aborted");
                throw new APIPPipelineExecutionFailedException("Pipeline execution failed: " + stepParameters.getName() + " failed!");
            } catch (APIPWrongYmlFileOfConnectorException ex){
                log.error("Failed to execute step! Failed to read yml file of connector! Message: " + ex.getMessage());
                throw ex;
            } catch (IOException exx){
                log.error("Failed to execute step! Retry mechanism failed! Message: " + exx.getMessage());
                throw new APIPRetryMechanismException(exx);
            }
        }

        log.info("Pipeline successfully executed!");
        return stateService.setState(pipelineExeId, "finished");
    }
}
