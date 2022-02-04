package com.example.projectfirst.connector;

import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;

import java.util.List;

public interface ConnectorInterface {
    List<ConnectorCollection> fetchAllConnectors();
    ConnectorCollection fetchConnector(String id);
    ConnectorCollection saveConnector(String yaml) throws APIPWrongYmlFileOfConnectorException;
    ConnectorCollection updateConnector(String yaml, String id);
    void deleteConnector(String id);
    void deleteConnectors();
}
