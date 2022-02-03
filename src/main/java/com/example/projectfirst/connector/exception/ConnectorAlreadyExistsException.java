package com.example.projectfirst.connector.exception;

public class ConnectorAlreadyExistsException extends RuntimeException{
    public ConnectorAlreadyExistsException(String id) {
        super("Connector with id: " + id + " already exists!");
    }
}
