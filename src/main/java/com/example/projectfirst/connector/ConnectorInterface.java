package com.example.projectfirst.connector;

import java.io.IOException;
import java.util.List;

public interface ConnectorInterface {
    List<ConnectorCollection> fetchAllConnectors();
    ConnectorCollection fetchConnector(String id);
    ConnectorCollection saveConnector(String yaml) throws IOException;
    ConnectorCollection updateConnector(String yaml, String id);
    ConnectorCollection deleteConnector(String id);
    void deleteConnectors();
}
