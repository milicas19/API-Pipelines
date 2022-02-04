package com.example.projectfirst.connector.exception;

public class APIPConnectorNotFoundException extends RuntimeException {
    public APIPConnectorNotFoundException(String id) {
        super("Could not find connector with id " + id + "!");
    }
}
