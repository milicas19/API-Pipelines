package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.connector.ConnectorService;
import com.example.projectfirst.pipeline.apiRequestHandler.GetHandler;
import com.example.projectfirst.pipeline.apiRequestHandler.PostHandler;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Optional;

@Service
public class ExecutionService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Autowired
    private ConnectorService connectorService;

    @Retryable(value = {RuntimeException.class, IOException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000,multiplier = 2))
    public Pair<String, String> executeStep(String pipelineExeId, StepParameters stepParameters) throws IOException{
        Optional<PipelineExecutionCollection> pipelineExecution =
                pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecution.isEmpty())
            throw new PipelineExecutionNotFoundException(pipelineExeId);

        String state = pipelineExecution.get().getState();

        if (!(state.equals("prepared") || state.equals("running")))
            return Pair.of(state, "");

        pipelineExecution.get().setState("running");
        pipelineExecutionRepository.save(pipelineExecution.get());

        switch (stepParameters.getType()) {
            case "API_GET":
                GetHandler getHandler = new GetHandler();
                Response responseGet = getHandler.execute(stepParameters, connectorService);
                return Pair.of(pipelineExecution.get().getState(), responseGet.body().string());
            case "API_POST":
                PostHandler postHandler = new PostHandler();
                Response responsePost = postHandler.execute(stepParameters, connectorService);
                return Pair.of(pipelineExecution.get().getState(), responsePost.body().string());
            default:
                return Pair.of("abort","");
        }
    }

}
