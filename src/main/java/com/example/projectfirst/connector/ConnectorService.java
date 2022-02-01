package com.example.projectfirst.connector;

import com.example.projectfirst.connector.exception.ConnectorAlreadyExistsException;
import com.example.projectfirst.connector.exception.ConnectorNotFoundException;
import com.example.projectfirst.connector.exception.ObjectMapperException;
import com.example.projectfirst.connector.model.Connector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
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

    public ConnectorCollection saveConnector(String yaml) throws IOException{
        // YAML to POJO
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Connector> connectorMap = objectMapper.readValue(yaml,
                    new TypeReference<>(){});
            Connector conn = connectorMap.get("connector");
            String id = conn.getId();

            if(connectorRepository.existsById(id)){
                throw new ConnectorAlreadyExistsException(id);
            }
            ConnectorCollection connector = new ConnectorCollection(id,yaml, LocalDateTime.now(), LocalDateTime.now());
            connectorRepository.save(connector);
            return connector;
        } catch (IOException e) {
                throw new ObjectMapperException();
        }
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

    public ConnectorCollection deleteConnector(String id) {
        return connectorRepository.findById(id)
                .map(connectorCollection -> {
                   connectorRepository.deleteById(id);
                   return connectorCollection;
                })
                .orElseThrow(() -> new ConnectorNotFoundException(id));
    }

    public void deleteConnectors() {
        connectorRepository.deleteAll();
    }

    public Connector getConnectorFromYml(String id) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String connectorYml = fetchConnector(id).getYmlFile();

        Map<String, Connector> connectorMap = objectMapper.readValue(connectorYml, new TypeReference<>(){});
        return connectorMap.get("connector");
    }
}
