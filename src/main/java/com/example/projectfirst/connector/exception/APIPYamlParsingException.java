package com.example.projectfirst.connector.exception;

import java.io.IOException;

public class APIPYamlParsingException extends IOException {
    public APIPYamlParsingException(String msg){
        super(msg);
    }
}
