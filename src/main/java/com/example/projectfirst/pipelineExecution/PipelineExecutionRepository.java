package com.example.projectfirst.pipelineExecution;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineExecutionRepository extends MongoRepository<PipelineExecutionCollection, String> {
}
