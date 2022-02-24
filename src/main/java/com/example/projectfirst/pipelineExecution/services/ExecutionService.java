package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.pipeline.apiRequestHandler.StepHandler;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipeline.registrar.StepRegistrar;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ExecutionService {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    private final Map<String, StepHandler> stepHandlerMap = new HashMap<>();

    @PostConstruct
    public void init()  {
        log.info("init() of ExecutionService!");
        Instant start = Instant.now();
        Reflections reflections =  new Reflections(
                new ConfigurationBuilder()
                        .forPackage("")
                        .setScanners(Scanners.SubTypes)
        );
        Set<Class<? extends StepRegistrar>> registrars = reflections.getSubTypesOf(StepRegistrar.class);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        log.info("Time taken to scan reflection: "+ timeElapsed.toMillis() +" milliseconds");

        for (Class<? extends StepRegistrar> registrar : registrars) {
            try {
                StepRegistrar stepRegistrar = registrar.getDeclaredConstructor().newInstance();
                Map<String, Class<? extends StepHandler>> registeredSteps = stepRegistrar.getRegisteredSteps();

                for (Map.Entry<String, Class<? extends StepHandler>> entry : registeredSteps.entrySet()) {
                    StepHandler stepHandler = entry.getValue().getDeclaredConstructor().newInstance();
                    beanFactory.autowireBean(stepHandler);
                    String key = entry.getKey();
                    this.stepHandlerMap.put(key, stepHandler);
                }
            } catch (InstantiationException |
                    NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }
    }

    public StepExecution executeStep(StepParameters stepParameters)
            throws APIPYamlParsingException, APIPStepExecutionFailedException {

        try {
            log.info("Executing " + stepParameters.getName() + "!");
            return this.stepHandlerMap.get(stepParameters.getType()).execute(stepParameters);
        }catch (APIPYamlParsingException ex){
            log.error("Failed to execute step! Failed to read yml file of connector! Message: " + ex.getMessage());
            throw new APIPYamlParsingException("Error while parsing connector from yaml input!");
        }
    }
}
