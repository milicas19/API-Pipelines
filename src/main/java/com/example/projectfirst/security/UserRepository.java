package com.example.projectfirst.security;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<MyUser, String> {
    boolean existsByUsername(String username);
    Optional<MyUser> findByUsername(String username);
}
