package com.example.projectfirst.pipeline;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PipelineRepository extends MongoRepository<PipelineCollection, String> {
    Optional<PipelineCollection> findById(String id);
    boolean existsById(String id);
    void deleteById(String id);
}
