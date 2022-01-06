package com.example.projectfirst.connector;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectorRepository extends MongoRepository<ConnectorCollection, String> {
}
