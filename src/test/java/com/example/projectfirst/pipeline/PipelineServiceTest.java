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
    void canFetchAllPipelines() {
        underTest.fetchAllPipelines();
        verify(pipelineRepository).findAll();
    }

    @Test
    void canFetchPipeline() {
        String ymlPipeline =
                "pipeline:\n" +
                "    id: pipeTest\n" +
                "    name: Pipeline\n" +
                "    description: This is my pipeline\n" +
                "    steps:\n" +
                "    -   name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://some-url\"\n" +
                "            connectorID: connTest\n" +
                "            output: some-output\n";
        LocalDateTime dateTime = LocalDateTime.now();
        String pipeId = "pipeTest";

        PipelineCollection pipelineTest = new PipelineCollection(pipeId, ymlPipeline, dateTime, dateTime);
        given(pipelineRepository.findById(any())).willReturn(Optional.of(pipelineTest));

        PipelineCollection fetchedPipeline = underTest.fetchPipeline(pipeId);

        assertThat(fetchedPipeline).isEqualTo(pipelineTest);
    }

    @Test
    void willThrowWhenPipelineNotFound(){
        String pipeId = "pipeTest";

        given(pipelineRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.fetchPipeline(pipeId))
                .isInstanceOf(APIPPipelineNotFoundException.class)
                .hasMessageContaining("Could not find pipeline with id " + pipeId + "!");

    }


    @Test
    void canSavePipeline() throws APIPYamlParsingException {
        String ymlPipeline =
                "pipeline:\n" +
                        "    id: pipeTest\n" +
                        "    name: Pipeline\n" +
                        "    description: This is my pipeline\n" +
                        "    steps:\n" +
                        "    -   name: step1\n" +
                        "        type: API_GET\n" +
                        "        spec:\n" +
                        "            url: \"https://some-url\"\n" +
                        "            connectorID: connTest\n" +
                        "            output: some-output\n";
        LocalDateTime dateTime = LocalDateTime.now();
        String pipeId = "pipeTest";
        PipelineCollection pipelineTest = new PipelineCollection(pipeId, ymlPipeline, dateTime, dateTime);

        PipelineCollection savedPipeline = underTest.savePipeline(ymlPipeline);

        ArgumentCaptor<PipelineCollection> connectorArgumentCaptor = ArgumentCaptor.forClass(PipelineCollection.class);
        verify(pipelineRepository).save(connectorArgumentCaptor.capture());

        PipelineCollection capturedPipeline = connectorArgumentCaptor.getValue();

        assertThat(capturedPipeline.getId()).isEqualTo(pipelineTest.getId());
        assertThat(capturedPipeline.getYmlFile()).isEqualTo(pipelineTest.getYmlFile());
        assertThat(savedPipeline.getId()).isEqualTo(pipelineTest.getId());
        assertThat(savedPipeline.getYmlFile()).isEqualTo(pipelineTest.getYmlFile());
    }

    @Test
    void willThrowWhenIdIsTaken(){
        String ymlPipeline =
                "pipeline:\n" +
                        "    id: pipeTest\n" +
                        "    name: Pipeline\n" +
                        "    description: This is my pipeline\n" +
                        "    steps:\n" +
                        "    -   name: step1\n" +
                        "        type: API_GET\n" +
                        "        spec:\n" +
                        "            url: \"https://some-url\"\n" +
                        "            connectorID: connTest\n" +
                        "            output: some-output\n";

        given(pipelineRepository.existsById(anyString())).willReturn(true);

        assertThatThrownBy(() -> underTest.savePipeline(ymlPipeline))
                .isInstanceOf(APIPPipelineAlreadyExistsException.class)
                .hasMessageContaining("Pipeline with id pipeTest already exists!");
    }

    @Test
    void willThrowWhenYamlIsNotCorrect() {
        // ymlPipeline missing "pipeline:" at the beginning
        String ymlPipeline =
                        "    id: pipeTest\n" +
                        "    name: Pipeline\n" +
                        "    description: This is my pipeline\n" +
                        "    steps:\n" +
                        "    -   name: step1\n" +
                        "        type: API_GET\n" +
                        "        spec:\n" +
                        "            url: \"https://some-url\"\n" +
                        "            connectorID: connTest\n" +
                        "            output: some-output\n";

        assertThatThrownBy(() -> underTest.savePipeline(ymlPipeline))
                .isInstanceOf(APIPYamlParsingException.class)
                .hasMessageContaining("Error while parsing pipeline from yaml input!");
    }

    @Test
    void canUpdateConnector() {
        String ymlPipeline =
                "pipeline:\n" +
                        "    id: pipeTest\n" +
                        "    name: Pipeline\n" +
                        "    description: This is my pipeline\n" +
                        "    steps:\n" +
                        "    -   name: step1\n" +
                        "        type: API_GET\n" +
                        "        spec:\n" +
                        "            url: \"https://some-url\"\n" +
                        "            connectorID: connTest\n" +
                        "            output: some-output\n";
        String newYmlPipeline =
                "pipeline:\n" +
                        "    id: pipeTest\n" +
                        "    name: Pipeline\n" +
                        "    description: This is my pipeline\n" +
                        "    steps:\n" +
                        "    -   name: step1\n" +
                        "        type: API_GET\n" +
                        "        spec:\n" +
                        "            url: \"https://some-url\"\n" +
                        "            connectorID: connTest\n" +
                        "            output: some-output\n" +
                        "        retry: 3\n" +
                        "        backOffPeriod: 2000";
        LocalDateTime dateTime = LocalDateTime.now();
        String pipeId = "pipeTest";
        PipelineCollection pipelineTest = new PipelineCollection(pipeId, ymlPipeline, dateTime, dateTime);

        given(pipelineRepository.findById(any())).willReturn(Optional.of(pipelineTest));

        pipelineTest.setYmlFile(newYmlPipeline);
        pipelineTest.setModificationDate(LocalDateTime.now());
        given(pipelineRepository.save(any())).willReturn(pipelineTest);

        PipelineCollection updatedPipeline = underTest.updatePipeline(newYmlPipeline, pipeId);

        assertThat(updatedPipeline).isEqualTo(pipelineTest);
    }

    @Test
    void canDeleteConnector() {
        String pipeId = "pipeTest";
        underTest.deletePipeline(pipeId);

        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(pipelineRepository).deleteById(idArgumentCaptor.capture());

        String capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(pipeId);
    }

    @Test
    void canDeleteConnectors() {
        underTest.deletePipelines();
        verify(pipelineRepository).deleteAll();
    }
}