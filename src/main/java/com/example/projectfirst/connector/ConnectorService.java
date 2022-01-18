package com.example.projectfirst.connector;

import com.example.projectfirst.connector.exception.ConnectorNotFoundException;
import com.example.projectfirst.connector.model.Connector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorService implements ConnectorInterface{

    @Autowired
    private ConnectorRepository connectorRepository;


    public List<ConnectorCollection> fetchAllConnectors() {
        return connectorRepository.findAll();
    }

    public ConnectorCollection fetchConnector(String id) {
        return connectorRepository.findById(id)
                .orElseThrow(() -> new ConnectorNotFoundException(id));
    }

    public String saveConnector(String yaml) {
        // YAML to POJO
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Connector> connectorMap = objectMapper.readValue(yaml,
                    new TypeReference<Map<String, Connector>>(){});
            Connector conn = connectorMap.get("connector");
            if(connectorRepository.existsById(conn.getId())){
                return "Connector with that id already exists!";
            }
            ConnectorCollection connector = new ConnectorCollection(conn.getId(),yaml, LocalDateTime.now(), LocalDateTime.now());
            connectorRepository.save(connector);
        } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
        }
        return "Successfully saved!";
    }

    public ConnectorCollection updateConnector(String yaml, String id) {
        return connectorRepository.findById(id)
                .map(connectorCollection -> {
                    connectorCollection.setYmlFile(yaml);
                    connectorCollection.setModificationDate(LocalDateTime.now());
                    return connectorRepository.save(connectorCollection);
                })
                .orElseThrow(() -> new ConnectorNotFoundException(id));
    }

    public String deleteConnector(String id) {
        if(connectorRepository.existsById(id)){
            connectorRepository.deleteById(id);
            return "Successfully deleted!";
        }
        throw new ConnectorNotFoundException(id);
    }

    public void deleteConnectors() {
        connectorRepository.deleteAll();
    }
}
