package com.example.projectfirst.pipeline;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.pipeline.exception.APIPPipelineAlreadyExistsException;
import com.example.projectfirst.pipeline.exception.APIPPipelineNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PipelineServiceTest {

    private PipelineService underTest;

    @Mock
    private PipelineRepository pipelineRepository;

    @BeforeEach
    void setUp(){
        underTest = new PipelineService(pipelineRepository);
    }

    @Test
    void canFetchAllConnectors() {
        underTest.fetchAllPipelines();
        verify(pipelineRepository).findAll();
    }

    @Test
    void canFetchConnector() {
        String ymlPipeline = "pipeline:\n" +
                "    id: pipeTest\n" +
                "    name: Pipeline\n" +
                "    description: This is my pipeline\n" +
                "    steps:\n" +
                "    -   name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://community-open-weather-map.p.rapidapi.com/weather?q=Belgrade%2C%20Serbia\"\n" +
                "            connectorID: 1\n" +
                "            output: <response_body|json.path(\"a\", response_body)>\n";
        LocalDateTime dateTime = LocalDateTime.now();
        String id = "pipeTest";
        PipelineCollection pipelineTest = new PipelineCollection(id, ymlPipeline, dateTime, dateTime);

        given(pipelineRepository.findById(any())).willReturn(Optional.of(pipelineTest));

        PipelineCollection fetchedPipeline = underTest.fetchPipeline(id);

        assertThat(fetchedPipeline.getYmlFile()).isEqualTo(pipelineTest.getYmlFile());
    }

    @Test
    void willThrowWhenConnectorNotFound(){
        String id = "pipeTest";

        given(pipelineRepository.findById(any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.fetchPipeline(id))
                .isInstanceOf(APIPPipelineNotFoundException.class)
                .hasMessageContaining("Could not find pipeline with id " + id + "!");

    }


    @Test
    void canSaveConnector() throws APIPYamlParsingException {
        String ymlPipeline = "pipeline:\n" +
                "    id: pipeTest\n" +
                "    name: Pipeline\n" +
                "    description: This is my pipeline\n" +
                "    steps:\n" +
                "    -   name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://community-open-weather-map.p.rapidapi.com/weather?q=Belgrade%2C%20Serbia\"\n" +
                "            connectorID: 1\n" +
                "            output: <response_body|json.path(\"a\", response_body)>\n";
        LocalDateTime dateTime = LocalDateTime.now();
        String id = "pipeTest";
        PipelineCollection pipelineTest = new PipelineCollection(id, ymlPipeline, dateTime, dateTime);

        given(pipelineRepository.save(any()))
                .willReturn(pipelineTest);

        PipelineCollection savedPipeline = underTest.savePipeline(ymlPipeline);

        assertThat(savedPipeline.getYmlFile()).isEqualTo(pipelineTest.getYmlFile());
    }

    @Test
    void willThrowWhenIdIsTaken(){
        String ymlPipeline = "pipeline:\n" +
                "    id: pipeTest\n" +
                "    name: Pipeline\n" +
                "    description: This is my pipeline\n" +
                "    steps:\n" +
                "    -   name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://community-open-weather-map.p.rapidapi.com/weather?q=Belgrade%2C%20Serbia\"\n" +
                "            connectorID: 1\n" +
                "            output: <response_body|json.path(\"a\", response_body)>\n";

        given(pipelineRepository.existsById(anyString()))
                .willReturn(true);

        assertThatThrownBy(() -> underTest.savePipeline(ymlPipeline))
                .isInstanceOf(APIPPipelineAlreadyExistsException.class)
                .hasMessageContaining("Pipeline with id pipeTest already exists!");
    }



    @Test
    void canUpdateConnector() {
        String ymlPipeline = "pipeline:\n" +
                "    id: pipeTest\n" +
                "    name: Pipeline\n" +
                "    description: This is my pipeline\n" +
                "    steps:\n" +
                "    -   name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://community-open-weather-map.p.rapidapi.com/weather?q=Belgrade%2C%20Serbia\"\n" +
                "            connectorID: 1\n" +
                "            output: <response_body|json.path(\"a\", response_body)>\n";
        String newYmlPipeline = "pipeline:\n" +
                "    id: pipeTest\n" +
                "    name: Pipeline\n" +
                "    description: This is my pipeline\n" +
                "    steps:\n" +
                "    -   name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://community-open-weather-map.p.rapidapi.com/weather?q=Belgrade%2C%20Serbia\"\n" +
                "            connectorID: 1\n" +
                "            output: <response_body|json.path(\"a\", response_body)>\n" +
                "        retry: 3\n" +
                "        backOffPeriod: 2000";
        LocalDateTime dateTime = LocalDateTime.now();
        String id = "pipeTest";
        PipelineCollection pipelineTest = new PipelineCollection(id, ymlPipeline, dateTime, dateTime);

        given(pipelineRepository.findById(any()))
                .willReturn(Optional.of(pipelineTest));

        pipelineTest.setYmlFile(newYmlPipeline);
        pipelineTest.setModificationDate(LocalDateTime.now());

        given(pipelineRepository.save(any()))
                .willReturn(pipelineTest);

        PipelineCollection updatedPipeline = underTest.updatePipeline(newYmlPipeline, id);

        assertThat(updatedPipeline.getYmlFile()).isEqualTo(newYmlPipeline);
    }

    @Test
    void canDeleteConnector() {
        String id = "pipeTest";
        underTest.deletePipeline(id);

        ArgumentCaptor<String> idArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        verify(pipelineRepository).deleteById(idArgumentCaptor.capture());

        String capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void canDeleteConnectors() {
        underTest.deletePipelines();
        verify(pipelineRepository).deleteAll();
    }
}