package com.example.projectfirst.pipeline.registrar;

import com.example.projectfirst.pipeline.apiRequestHandler.StepHandler;
import java.util.Map;

public interface StepRegistrar {
     Map<String, Class<? extends StepHandler>> getRegisteredSteps();
}
