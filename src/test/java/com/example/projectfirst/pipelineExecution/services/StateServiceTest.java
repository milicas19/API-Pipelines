package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.exceptions.APIPPipelineExecutionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StateServiceTest {

    private StateService underTest;
    @Mock
    private PipelineExecutionRepository pipelineExecutionRepository;

    @BeforeEach
    void setUp(){
        underTest = new StateService(pipelineExecutionRepository);
    }

    @Test
    void canCheckState() {
        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "prepared","Execution of pipeline is prepared!", new ArrayList<>(), new HashMap<>(), 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        String fetchedState = underTest.checkState(pipelineExecutionTest.getId());

        assertThat(fetchedState).isEqualTo(pipelineExecutionTest.getState());
    }

    @Test
    void willThrowWhenExecutionNotFoundWhileCheckingState(){
        String id = "pipeExeId";

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.checkState(id))
                .isInstanceOf(APIPPipelineExecutionNotFoundException.class)
                .hasMessageContaining("Could not find pipeline execution with id " + id + "!");
    }

    @Test
    void canSetState() {
        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "prepared", "Execution of pipeline is prepared!",new ArrayList<>(),
                new HashMap<>(), 0);
        String state = "running";

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        underTest.setStateAndDescription(pipelineExecutionTest.getId(),state, "some description");

        ArgumentCaptor<PipelineExecutionCollection> pipelineExecutionArgumentCaptor
                = ArgumentCaptor.forClass(PipelineExecutionCollection.class);

        verify(pipelineExecutionRepository).save(pipelineExecutionArgumentCaptor.capture());

        PipelineExecutionCollection capturedPipelineExecution = pipelineExecutionArgumentCaptor.getValue();

        pipelineExecutionTest.setState(state);
        pipelineExecutionTest.setDescription("some description");

        assertThat(capturedPipelineExecution).isEqualTo(pipelineExecutionTest);
    }

    @Test
    void willThrowWhenExecutionNotFoundWhileSettingState(){
        String id = "pipeExeId";
        String state = "running";

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.setStateAndDescription(id, state, "some-desc"))
                .isInstanceOf(APIPPipelineExecutionNotFoundException.class)
                .hasMessageContaining("Could not find pipeline execution with id " + id + "!");
    }
}