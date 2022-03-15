package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipeline.apiRequestHandler.SpecGet;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.exceptions.APIPInitiateExecutionFailed;
import com.example.projectfirst.exceptions.APIPPipelineExecutionFailedException;
import com.example.projectfirst.exceptions.APIPPipelineExecutionNotFoundException;
import com.example.projectfirst.exceptions.APIPPipelineNotPausedException;
import com.example.projectfirst.pipelineExecution.services.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PipelineExecutionServiceTest {

    private PipelineExecutionService underTest;
    @Mock
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Mock
    private WorkflowService workflowService;

    @BeforeEach
    void setUp(){
        underTest = new PipelineExecutionService(pipelineExecutionRepository, workflowService);
    }

    @Test
    void canFetchAllExecutions() {
        underTest.fetchAllExecutions();
        verify(pipelineExecutionRepository).findAll();
    }

    @Test
    void canFetchExecution() {
        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "prepared", "Execution of pipeline is prepared!", new ArrayList<>(), new HashMap<>(), 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        PipelineExecutionCollection fetchedExecution = underTest.fetchExecution(pipelineExecutionTest.getId());

        assertThat(fetchedExecution).isEqualTo(pipelineExecutionTest);
    }

    @Test
    void willThrowWhenExecutionNotFound(){
        String id = "pipeExeTest";

        given(pipelineExecutionRepository.findById(any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.fetchExecution(id))
                .isInstanceOf(APIPPipelineExecutionNotFoundException.class)
                .hasMessageContaining("Could not find pipeline execution with id " + id + "!");

    }

    @Test
    void canFetchPausedExecutions() {
        PipelineExecutionCollection pausedPipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "paused", "Execution of pipeline is prepared!", new ArrayList<>(), new HashMap<>(), 0);
        PipelineExecutionCollection notPausedPipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "finished", "Pipeline successfully executed!", new ArrayList<>(), new HashMap<>(), 0);
        List<PipelineExecutionCollection> allPipelineExecutionTest = new ArrayList<>();
        allPipelineExecutionTest.add(pausedPipelineExecutionTest);
        allPipelineExecutionTest.add(notPausedPipelineExecutionTest);

        given(pipelineExecutionRepository.findAll()).willReturn(allPipelineExecutionTest);

        List<PipelineExecutionCollection> fetchedPausedExecutions = underTest.fetchPausedExecutions();

        assertThat(fetchedPausedExecutions.get(0)).isEqualTo(pausedPipelineExecutionTest);
    }

    @Test
    void canDeleteExecution() {
        String id = "pipeExeTest";
        underTest.deleteExecution(id);

        ArgumentCaptor<String> idArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        verify(pipelineExecutionRepository).deleteById(idArgumentCaptor.capture());

        String capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void canDeleteExecutions() {
        underTest.deleteExecutions();
        verify(pipelineExecutionRepository).deleteAll();
    }

    @Test
    void canExecutePipeline() throws APIPInitiateExecutionFailed {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        List<StepParameters> steps = new ArrayList<>();
        steps.add(new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{some-expr}"),
                2, 3000));

        HashMap<String, String> output = new HashMap<>();
        LocalDateTime dateTime = LocalDateTime.now();

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineId, dateTime,
                "prepared", "Execution of pipeline is prepared!", steps, output, 0);
        given(workflowService.initiateExecution(any())).willReturn(pipelineExecutionTestId);

        output.put("step1", "some-output");
        PipelineExecutionCollection expectedPipelineExecution = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime, "finished", "Pipeline successfully executed!", steps, output, 1);

        String pipelineExeId = underTest.executePipeline(pipelineId);

        assertThat(pipelineExeId).isEqualTo(pipelineExecutionTestId);

    }

    @Test
    void willThrowWhenInitiationOfPipelineFails() throws APIPInitiateExecutionFailed {
        String pipelineId = "pipeTest";
        given(workflowService.initiateExecution(any())).willThrow(APIPInitiateExecutionFailed.class);

        assertThatThrownBy(() -> underTest.executePipeline(pipelineId))
                .isInstanceOf(APIPPipelineExecutionFailedException.class)
                .hasMessageContaining("Pipeline execution initiation failed! " +
                        "Something wrong with the yml file of pipeline!");
    }

    @Test
    void canResumeExecution() {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        List<StepParameters> steps = new ArrayList<>();
        steps.add(new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{some-expr}"),
                2, 3000));

        HashMap<String, String> output = new HashMap<>();
        LocalDateTime dateTime = LocalDateTime.now();

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime, "paused", "Execution of pipeline is paused!", steps, output, 0);
        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        output.put("step1", "some-output");
        PipelineExecutionCollection expectedPipelineExecution = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime, "finished", "Pipeline successfully executed!", steps, output, 1);

        String pipelineExeId = underTest.resumeExecution(pipelineExecutionTestId);

        assertThat(pipelineExeId).isEqualTo(pipelineExecutionTestId);
    }

    @Test
    void willThrowWhenPipelineExecutionNotFound() {
        String pipelineExecutionTestId = "pipeExeTest";

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.resumeExecution(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineExecutionNotFoundException.class)
                .hasMessageContaining("Could not find pipeline execution with id " + pipelineExecutionTestId + "!");
    }

    @Test
    void willThrowWhenPipelineExecutionNotPaused() throws APIPInitiateExecutionFailed {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, LocalDateTime.now(), "aborted", "Execution of pipeline is aborted!", new ArrayList<>(), new HashMap<>(), 0);
        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        assertThatThrownBy(() -> underTest.resumeExecution(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineNotPausedException.class)
                .hasMessageContaining("Pipeline execution with id " + pipelineExecutionTestId + " is not paused!");
    }
}