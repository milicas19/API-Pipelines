package com.example.projectfirst.pipeline;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRepository extends MongoRepository<PipelineCollection, String> {
}
