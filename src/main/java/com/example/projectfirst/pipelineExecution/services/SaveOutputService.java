package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class SaveOutputService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public void save(String pipelineExeId, String response, String name){
        Optional<PipelineExecutionCollection> pipelineExecution =
                pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecution.isEmpty())
            throw new PipelineExecutionNotFoundException(pipelineExeId);

        JSONObject json = new JSONObject(response);
        System.out.println(json.toString(4));

        HashMap<String, String> output = pipelineExecution.get().getOutput();
        output.put(name, json.toString(4));

        pipelineExecution.get().setOutput(output);

        pipelineExecution.get().setState("running");

        Integer numberOfExecutedSteps = pipelineExecution.get().getNumberOfExecutedSteps();
        numberOfExecutedSteps += 1;
        pipelineExecution.get().setNumberOfExecutedSteps(numberOfExecutedSteps);

        pipelineExecutionRepository.save(pipelineExecution.get());
    }
}
