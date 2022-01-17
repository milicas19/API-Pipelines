package com.example.projectfirst.pipeline;

import com.example.projectfirst.connector.ConnectorService;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;


public interface StepHandler {
    Response execute(StepParameters stepParameter,
                     ConnectorService connectorService) throws IOException;
}
