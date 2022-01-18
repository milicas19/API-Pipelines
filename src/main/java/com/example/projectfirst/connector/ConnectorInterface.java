package com.example.projectfirst.connector;

import java.util.List;

public interface ConnectorInterface {
    List<ConnectorCollection> fetchAllConnectors();
    ConnectorCollection fetchConnector(String id);
    String saveConnector(String yaml);
    ConnectorCollection updateConnector(String yaml, String id);
    String deleteConnector(String id);
    void deleteConnectors();
}
