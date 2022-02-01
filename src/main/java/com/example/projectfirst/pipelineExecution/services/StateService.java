package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public String checkState(String pipelineExeId) {
        return pipelineExecutionRepository.findById(pipelineExeId)
                .map(PipelineExecutionCollection::getState)
                .orElseThrow(()-> new PipelineExecutionNotFoundException(pipelineExeId));
    }

    public PipelineExecutionCollection setState(String pipelineExeId, String state) {
        return pipelineExecutionRepository.findById(pipelineExeId)
                .map(pipelineExecution -> {
                    pipelineExecution.setState(state);
                    pipelineExecutionRepository.save(pipelineExecution);
                    return pipelineExecution;
                })
                .orElseThrow(()-> new PipelineExecutionNotFoundException(pipelineExeId));
    }
}
