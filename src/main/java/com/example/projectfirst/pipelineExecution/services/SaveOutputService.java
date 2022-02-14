package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class SaveOutputService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public void save(String pipelineExeId, String response, String name){
        log.info("Execution successful! Saving result of this step!");
        pipelineExecutionRepository.findById(pipelineExeId)
                .map(pipelineExecution -> {
                    HashMap<String, String> output = pipelineExecution.getOutput();
                    output.put(name, response);
                    log.info("response: " + response);
                    pipelineExecution.setOutput(output);

                    Integer numberOfExecutedSteps = pipelineExecution.getNumberOfExecutedSteps();
                    numberOfExecutedSteps += 1;
                    pipelineExecution.setNumberOfExecutedSteps(numberOfExecutedSteps);

                    pipelineExecutionRepository.save(pipelineExecution);
                    return null;
                });
    }
}
