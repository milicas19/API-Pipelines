package com.example.projectfirst.pipelineExecution;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PipelineExecutionRepository extends MongoRepository<PipelineExecutionCollection, String> {
}
