package com.example.projectfirst.connector.exception;

public class APIPConnectorAlreadyExistsException extends RuntimeException{
    public APIPConnectorAlreadyExistsException(String id) {
        super("Connector with id " + id + " already exists!");
    }
}
