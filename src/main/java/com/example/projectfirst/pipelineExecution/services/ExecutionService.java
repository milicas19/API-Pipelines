package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipeline.apiRequestHandler.StepHandler;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipeline.registrar.StepRegistrar;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionFailedException;
import com.squareup.okhttp.Response;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ExecutionService {

    @Autowired
    private SaveOutputService saveOutputService;
    @Autowired
    private StateService stateService;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private final Map<String, StepHandler> stepHandlerMap = new HashMap<>();

    @PostConstruct
    public void init()  {
        Reflections reflections = new Reflections(
                "com.example.projectfirst.pipeline", Scanners.SubTypes);
        Set<Class<? extends StepRegistrar>> registrars = reflections.getSubTypesOf(StepRegistrar.class);
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
                e.printStackTrace();
            }
        }
    }

    public void executeStep(String pipelineExeId, StepParameters stepParameters) throws IOException {

        Response response = this.stepHandlerMap.get(stepParameters.getType()).execute(stepParameters);

        if(response.isSuccessful()){
            saveOutputService.save(pipelineExeId, response.body().string(), stepParameters.getName());
        }else{
            stateService.setState(pipelineExeId, "aborted");
            throw new PipelineExecutionFailedException("Pipeline execution failed: " + stepParameters.getName() + " failed!");
        }
    }

}
