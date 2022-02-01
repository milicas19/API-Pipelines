package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class SaveOutputService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public void save(String pipelineExeId, String response, String name){
        pipelineExecutionRepository.findById(pipelineExeId)
                .map(pipelineExecution -> {
                    JSONObject json = new JSONObject(response);
                    HashMap<String, String> output = pipelineExecution.getOutput();
                    output.put(name, json.toString(4));
                    pipelineExecution.setOutput(output);

                    pipelineExecution.setState("running");

                    Integer numberOfExecutedSteps = pipelineExecution.getNumberOfExecutedSteps();
                    numberOfExecutedSteps += 1;
                    pipelineExecution.setNumberOfExecutedSteps(numberOfExecutedSteps);

                    pipelineExecutionRepository.save(pipelineExecution);
                    return null;
                });
    }
}
