package com.example.projectfirst.connector;

import com.example.projectfirst.connector.exception.APIPConnectorAlreadyExistsException;
import com.example.projectfirst.connector.exception.APIPConnectorNotFoundException;
import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

        ConnectorCollection fetchedConnector = underTest.fetchConnector(id);

        assertThat(fetchedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
    }

    @Test
    void willThrowWhenConnectorNotFound(){
        String id = "connTest";

        given(connectorRepository.findById(any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.fetchConnector(id))
                .isInstanceOf(APIPConnectorNotFoundException.class)
                .hasMessageContaining("Could not find connector with id " + id + "!");
    }


    @Test
    void canSaveConnector() throws APIPYamlParsingException {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);

        given(connectorRepository.save(any()))
                .willReturn(connectorTest);

        ConnectorCollection savedConnector = underTest.saveConnector(ymlConnector);

        assertThat(savedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
    }

    @Test
    void willThrowWhenIdIsTaken(){
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";

        String id = "connTest";
        given(connectorRepository.existsById(anyString()))
                .willReturn(true);

        assertThatThrownBy(() -> underTest.saveConnector(ymlConnector))
                .isInstanceOf(APIPConnectorAlreadyExistsException.class)
                .hasMessageContaining("Connector with id " + id + " already exists!");
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
        String id = "connTest";
        underTest.deleteConnector(id);

        ArgumentCaptor<String> idArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        verify(connectorRepository).deleteById(idArgumentCaptor.capture());

        String capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void canDeleteConnectors() {
        underTest.deleteConnectors();
        verify(connectorRepository).deleteAll();
    }
    
}