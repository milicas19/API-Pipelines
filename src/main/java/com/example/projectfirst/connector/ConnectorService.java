package com.example.projectfirst.connector;

import com.example.projectfirst.connector.exception.APIPConnectorAlreadyExistsException;
import com.example.projectfirst.connector.exception.APIPConnectorNotFoundException;
import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;
import com.example.projectfirst.connector.model.Connector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ConnectorService implements ConnectorInterface{

    @Autowired
    private ConnectorRepository connectorRepository;

    public List<ConnectorCollection> fetchAllConnectors() {
        log.info("Fetching all connectors!");
        return connectorRepository.findAll();
    }

    public ConnectorCollection fetchConnector(String id){
        log.info("Fetching connector with id " + id +"!");
        return connectorRepository.findById(id)
                .orElseThrow(() -> new APIPConnectorNotFoundException(id));
    }

    public ConnectorCollection saveConnector(String yaml) throws APIPWrongYmlFileOfConnectorException {
        log.info("Saving connector!");
        // YAML to POJO
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Connector> connectorMap = objectMapper.readValue(yaml,
                    new TypeReference<>(){});
            Connector conn = connectorMap.get("connector");
            String id = conn.getId();

            if(connectorRepository.existsById(id)){
                throw new APIPConnectorAlreadyExistsException(id);
            }
            ConnectorCollection connector = new ConnectorCollection(id,yaml, LocalDateTime.now(), LocalDateTime.now());
            connectorRepository.save(connector);
            log.info("Connector with id: " + id + " successfully saved!");
            return connector;
        } catch (IOException e) {
            log.error("Failed to save connector! Message: " + e.getMessage());
            throw new APIPWrongYmlFileOfConnectorException(e);
        }
    }

    public ConnectorCollection updateConnector(String yaml, String id) {
        log.info("Updating connector with id: " + id + "!");
        return connectorRepository.findById(id)
                .map(connectorCollection -> {
                    connectorCollection.setYmlFile(yaml);
                    connectorCollection.setModificationDate(LocalDateTime.now());
                    log.info("Connector successfully updated!");
                    return connectorRepository.save(connectorCollection);
                })
                .orElseThrow(() -> new APIPConnectorNotFoundException(id));
    }

    public void deleteConnector(String id) {
        log.info("Deleting connector with id " + id + "!");
        connectorRepository.deleteById(id);
    }

    public void deleteConnectors() {
        log.info("Deleting all connectors!");
        connectorRepository.deleteAll();
    }
}
