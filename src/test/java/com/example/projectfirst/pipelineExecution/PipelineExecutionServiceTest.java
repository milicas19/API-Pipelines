package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionNotFoundException;
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
                LocalDateTime.now(), "prepared", new ArrayList<>(), new HashMap<>(), 0);

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
                LocalDateTime.now(), "paused", new ArrayList<>(), new HashMap<>(), 0);
        PipelineExecutionCollection notPausedPipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "finished", new ArrayList<>(), new HashMap<>(), 0);
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
}