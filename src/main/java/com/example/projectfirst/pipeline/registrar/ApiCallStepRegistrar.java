package com.example.projectfirst.pipeline.registrar;

import com.example.projectfirst.pipeline.apiRequestHandler.GetHandler;
import com.example.projectfirst.pipeline.apiRequestHandler.PostHandler;
import com.example.projectfirst.pipeline.apiRequestHandler.StepHandler;
import java.util.HashMap;
import java.util.Map;

public class ApiCallStepRegistrar implements StepRegistrar{

    @Override
    public  Map<String, Class<? extends StepHandler>> getRegisteredSteps() {
        Map<String, Class<? extends StepHandler>> map = new HashMap<>();
        map.put("API_GET", GetHandler.class);
        map.put("API_POST", PostHandler.class);

        return map;
    }
}
