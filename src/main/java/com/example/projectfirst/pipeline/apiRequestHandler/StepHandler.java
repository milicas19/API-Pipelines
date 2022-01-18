package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.ConnectorService;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.squareup.okhttp.Response;

import java.io.IOException;


public interface StepHandler {
    Response execute(StepParameters stepParameter,
                     ConnectorService connectorService) throws IOException;
}
