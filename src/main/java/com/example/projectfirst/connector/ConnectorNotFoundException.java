package com.example.projectfirst.connector;

public class ConnectorNotFoundException extends RuntimeException {
    public ConnectorNotFoundException(String id) {
        super("Could not find connector with id: " + id + "!");
    }
}
