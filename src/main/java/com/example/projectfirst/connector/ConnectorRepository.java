package com.example.projectfirst.connector;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  ConnectorRepository extends MongoRepository<ConnectorCollection, String> {
}
