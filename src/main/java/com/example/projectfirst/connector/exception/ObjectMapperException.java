package com.example.projectfirst.connector.exception;

import java.io.IOException;

public class ObjectMapperException extends IOException {
    public ObjectMapperException(){
        super("Could not read yml file of connector!");
    }
}
