package com.example.projectfirst.connector;

import com.example.projectfirst.connector.exception.ConnectorAlreadyExistsException;
import com.example.projectfirst.connector.exception.ConnectorNotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConnectorServiceTest {

    private ConnectorService underTest;

    @Mock
    private ConnectorRepository connectorRepository;

    @BeforeEach
    void setUp(){
        underTest = new ConnectorService(connectorRepository);
    }

    @Test
    void canFetchAllConnectors() {
        underTest.fetchAllConnectors();
        verify(connectorRepository).findAll();
    }

    @Test
    void canFetchConnector() {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        String id = "connTest";
        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);
        given(connectorRepository.findById(any())).willReturn(Optional.of(connectorTest));

        ConnectorCollection fetchedConnector= underTest.fetchConnector(id);

        verify(connectorRepository).findById(any());

        AssertionsForClassTypes.assertThat(fetchedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
    }

    @Test
    void willThrowWhenConnectorNotFound(){
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);
        String id = connectorTest.getId();

        given(connectorRepository.findById(any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.fetchConnector(id))
                .isInstanceOf(ConnectorNotFoundException.class)
                .hasMessageContaining("Could not find connector with id: " + id + "!");

    }


    @Test
    void canSaveConnector() throws IOException {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);
        String id = connectorTest.getId();

        given(connectorRepository.save(any()))
                .willReturn(connectorTest);

        ConnectorCollection savedConnector = underTest.saveConnector(ymlConnector);

        assertThat(savedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
    }

    @Test
    void willThrowWhenIdIsTaken() throws IOException {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";

        given(connectorRepository.existsById(anyString()))
                .willReturn(true);

        assertThatThrownBy(() -> underTest.saveConnector(ymlConnector))
                .isInstanceOf(ConnectorAlreadyExistsException.class)
                .hasMessageContaining("Connector with id: connTest already exists!");
    }



    @Test
    void canUpdateConnector() {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        String newYmlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: NEW!!! Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);
        String id = connectorTest.getId();

        given(connectorRepository.findById(any()))
                .willReturn(Optional.of(connectorTest));

        connectorTest.setYmlFile(newYmlConnector);
        connectorTest.setModificationDate(LocalDateTime.now());
        given(connectorRepository.save(any()))
                .willReturn(connectorTest);

        ConnectorCollection updatedConnector = underTest.updateConnector(newYmlConnector, id);

        assertThat(updatedConnector.getYmlFile()).isEqualTo(newYmlConnector);
    }

    @Test
    void canDeleteConnector() {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);
        String id = connectorTest.getId();

        given(connectorRepository.findById(any()))
                .willReturn(Optional.of(connectorTest));

        ConnectorCollection deletedConnector = underTest.deleteConnector(id);

        assertThat(deletedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
    }

    @Test
    void canDeleteConnectors() {
        underTest.deleteConnectors();
        verify(connectorRepository).deleteAll();
    }
    
}