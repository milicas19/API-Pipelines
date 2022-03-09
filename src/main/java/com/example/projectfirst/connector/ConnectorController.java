package com.example.projectfirst.connector;

import com.example.projectfirst.exceptions.APIPYamlParsingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<ConnectorCollection> saveConnector(@RequestBody String yaml) throws APIPYamlParsingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(connectorService.saveConnector(yaml));
    }

    @PutMapping("/connectors/{id}")
    public ConnectorCollection updateConnector(@RequestBody String yaml, @PathVariable(value="id") String id){
        return connectorService.updateConnector(yaml,id);
    }

    @DeleteMapping("/connectors/{id}")
    public void deleteConnector(@PathVariable(value="id") String id){
        connectorService.deleteConnector(id);
    }

    @DeleteMapping("/connectors")
    public void deleteConnectors(){
        connectorService.deleteConnectors();
    }
}
