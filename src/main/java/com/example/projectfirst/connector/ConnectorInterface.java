package com.example.projectfirst.connector;

import com.example.projectfirst.exceptions.APIPYamlParsingException;

import java.util.List;

public interface ConnectorInterface {
    List<ConnectorCollection> fetchAllConnectors();
    ConnectorCollection fetchConnector(String id);
    ConnectorCollection saveConnector(String yaml) throws APIPYamlParsingException;
    ConnectorCollection updateConnector(String yaml, String id);
    void deleteConnector(String id);
    void deleteConnectors();
}
