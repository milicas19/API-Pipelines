package com.example.projectfirst.pipelineExecution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StateService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public String checkState(String pipelineExeId) {
        Optional<PipelineExecutionCollection> pipelineExecution =
                pipelineExecutionRepository.findById(pipelineExeId);

        return pipelineExecution.get().getState();
    }

    public void setState(String pipelineExeId, String state) {
        Optional<PipelineExecutionCollection> pipelineExecution =
                pipelineExecutionRepository.findById(pipelineExeId);

        pipelineExecution.get().setState(state);
        pipelineExecutionRepository.save(pipelineExecution.get());
    }
}
