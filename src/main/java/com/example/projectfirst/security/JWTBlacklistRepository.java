package com.example.projectfirst.security;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JWTBlacklistRepository extends MongoRepository<JWT, String> {
    Boolean existsByJwtToken(String token);
}
