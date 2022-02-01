package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.pipeline.model.StepParameters;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

public class GetHandler implements StepHandler{
    @Autowired
    private StepService stepService;

    @Override
    public Response execute(StepParameters stepParameter) throws IOException {
        return stepService.executeGetRequest(stepParameter);
    }
}
