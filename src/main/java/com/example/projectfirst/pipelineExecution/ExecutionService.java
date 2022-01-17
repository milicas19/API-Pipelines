package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.connector.ConnectorService;
import com.example.projectfirst.pipeline.GetHandler;
import com.example.projectfirst.pipeline.PostHandler;
import com.example.projectfirst.pipeline.StepParameters;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Optional;

@Service
public class ExecutionService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Autowired
    private ConnectorService connectorService;

    public Pair<String, String> executeStep(String pipelineExeId, StepParameters stepParameters) {
        Optional<PipelineExecutionCollection> pipelineExecution =
                pipelineExecutionRepository.findById(pipelineExeId);

        String state = pipelineExecution.get().getState();

        if (!(state.equals("prepared") || state.equals("running")))
            return Pair.of(state, "");

        pipelineExecution.get().setState("running");
        pipelineExecutionRepository.save(pipelineExecution.get());

        switch (stepParameters.getType()) {
            case "API_GET":
                GetHandler getHandler = new GetHandler();
                try {
                    Response response = getHandler.execute(stepParameters, connectorService);
                    return Pair.of(pipelineExecution.get().getState(), response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Pair.of(e.getMessage(), "");
                }
            case "API_POST":
                PostHandler postHandler = new PostHandler();
                try {
                    Response response = postHandler.execute(stepParameters, connectorService);
                    return Pair.of(pipelineExecution.get().getState(), response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Pair.of(e.getMessage(), "");
                }
        }
        return Pair.of(state, "");
    }

}
