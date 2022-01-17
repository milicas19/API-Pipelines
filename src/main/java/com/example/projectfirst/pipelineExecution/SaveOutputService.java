package com.example.projectfirst.pipelineExecution;

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

        HashMap<String, String> output = pipelineExecution.get().getOutput();
        output.put(name, response);
        pipelineExecution.get().setOutput(output);

        pipelineExecution.get().setState("running");

        Integer numberOfExecutedSteps = pipelineExecution.get().getNumberOfExecutedSteps();
        numberOfExecutedSteps += 1;
        pipelineExecution.get().setNumberOfExecutedSteps(numberOfExecutedSteps);

        pipelineExecutionRepository.save(pipelineExecution.get());
    }
}
