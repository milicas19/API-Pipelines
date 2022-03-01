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
        String connectorId = "connTest";
        String ymlConnector =
                "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();

        ConnectorCollection connectorTest = new ConnectorCollection(connectorId, ymlConnector, dateTime, dateTime);
        given(connectorRepository.findById(any())).willReturn(Optional.of(connectorTest));

        ConnectorCollection fetchedConnector = underTest.fetchConnector(connectorId);

        assertThat(fetchedConnector).isEqualTo(connectorTest);
    }

    @Test
    void willThrowWhenConnectorNotFound(){
        String connectorId = "connTest";

        given(connectorRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.fetchConnector(connectorId))
                .isInstanceOf(APIPConnectorNotFoundException.class)
                .hasMessageContaining("Could not find connector with id " + connectorId + "!");
    }


    @Test
    void canSaveConnector() throws APIPYamlParsingException {
        String ymlConnector =
                "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();

        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);

        ConnectorCollection savedConnector = underTest.saveConnector(ymlConnector);

        ArgumentCaptor<ConnectorCollection> connectorArgumentCaptor
                = ArgumentCaptor.forClass(ConnectorCollection.class);
        verify(connectorRepository).save(connectorArgumentCaptor.capture());

        ConnectorCollection capturedConnector = connectorArgumentCaptor.getValue();

        assertThat(capturedConnector.getId()).isEqualTo(connectorTest.getId());
        assertThat(capturedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
        assertThat(savedConnector.getId()).isEqualTo(connectorTest.getId());
        assertThat(savedConnector.getYmlFile()).isEqualTo(connectorTest.getYmlFile());
    }

    @Test
    void willThrowWhenIdIsTaken(){
        String connectorId = "connTest";
        String ymlConnector =
                "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";

        given(connectorRepository.existsById(anyString())).willReturn(true);

        assertThatThrownBy(() -> underTest.saveConnector(ymlConnector))
                .isInstanceOf(APIPConnectorAlreadyExistsException.class)
                .hasMessageContaining("Connector with id " + connectorId + " already exists!");
    }

    @Test
    void willThrowWhenYamlIsNotCorrect() {
        // ymlConnector missing "connector:" at the beginning
        String ymlConnector =
                        "    id: connTest\n" +
                        "    name: Test Connector\n" +
                        "    type: NO_AUTH\n" +
                        "    spec: \n" +
                        "        host: test-host";

        assertThatThrownBy(() -> underTest.saveConnector(ymlConnector))
                .isInstanceOf(APIPYamlParsingException.class)
                .hasMessageContaining("Error while parsing connector from yaml input!");
    }

    @Test
    void canUpdateConnector() {
        String ymlConnector =
                "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        String newYmlConnector =
                "connector:\n" +
                "    id: connTest\n" +
                "    name: NEW!!! Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection("connTest", ymlConnector, dateTime, dateTime);
        String connectorId = connectorTest.getId();

        given(connectorRepository.findById(any())).willReturn(Optional.of(connectorTest));

        connectorTest.setYmlFile(newYmlConnector);
        connectorTest.setModificationDate(LocalDateTime.now());
        given(connectorRepository.save(any())).willReturn(connectorTest);

        ConnectorCollection updatedConnector = underTest.updateConnector(newYmlConnector, connectorId);

        assertThat(updatedConnector.getYmlFile()).isEqualTo(newYmlConnector);
    }

    @Test
    void canDeleteConnector() {
        String connectorId = "connTest";
        underTest.deleteConnector(connectorId);

        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(connectorRepository).deleteById(idArgumentCaptor.capture());

        String capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(connectorId);
    }

    @Test
    void canDeleteConnectors() {
        underTest.deleteConnectors();
        verify(connectorRepository).deleteAll();
    }
    
}