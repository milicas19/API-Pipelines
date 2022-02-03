package com.example.projectfirst.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

import java.util.List;

@RestController
public class ConnectorController {

    @Autowired
    private ConnectorService connectorService;

    @GetMapping("/connectors")
    public List<ConnectorCollection> getConnectors(){
        return connectorService.fetchAllConnectors();
    }

    @GetMapping("/connectors/{id}")
    public ConnectorCollection getConnector(@PathVariable(value="id") String id){
        return connectorService.fetchConnector(id);
    }

    @PostMapping("/connectors")
    public ConnectorCollection postConnector(@RequestBody String yaml) throws IOException {
        return connectorService.saveConnector(yaml);
    }

    @PutMapping("/connectors/{id}")
    public ConnectorCollection putConnector(@RequestBody String yaml, @PathVariable(value="id") String id){
        return connectorService.updateConnector(yaml,id);
    }

    @DeleteMapping("/connectors/{id}")
    public ConnectorCollection deleteConnector(@PathVariable(value="id") String id){
        return connectorService.deleteConnector(id);
    }

    @DeleteMapping("/connectors")
    public void deleteConnectors(){
        connectorService.deleteConnectors();
    }
}
