package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import org.json.JSONObject;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaveOutputServiceTest {

    private SaveOutputService underTest;
    @Mock
    private PipelineExecutionRepository pipelineExecutionRepository;

    @BeforeEach
    void setUp(){
        underTest = new SaveOutputService(pipelineExecutionRepository);
    }

    @Test
    void canSave() {
        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "prepared", new ArrayList<>(), new HashMap<>(), 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        String response = "{\"some\": \"response\"}";
        String name = "step";

        underTest.save(pipelineExecutionTest.getId(), response, name);

        ArgumentCaptor<PipelineExecutionCollection> pipelineExecutionArgumentCaptor
                = ArgumentCaptor.forClass(PipelineExecutionCollection.class);

        verify(pipelineExecutionRepository).save(pipelineExecutionArgumentCaptor.capture());

        PipelineExecutionCollection capturedPipelineExecution= pipelineExecutionArgumentCaptor.getValue();

        JSONObject json = new JSONObject(response);
        HashMap<String, String> output = pipelineExecutionTest.getOutput();
        output.put(name, json.toString(4));
        pipelineExecutionTest.setOutput(output);

        int numberOfExecutedSteps = pipelineExecutionTest.getNumberOfExecutedSteps();
        numberOfExecutedSteps += 1;
        pipelineExecutionTest.setNumberOfExecutedSteps(numberOfExecutedSteps);

        assertThat(capturedPipelineExecution).isEqualTo(pipelineExecutionTest);
    }
}