package com.example.projectfirst.exceptions;

import java.io.IOException;

public class APIPYamlParsingException extends IOException {
    public APIPYamlParsingException(String msg){
        super(msg);
    }
}
