package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.pipeline.apiRequestHandler.SpecPost;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.APIPExpressionResolverException;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
class ExpressionResolverServiceTest {

    private ExpressionResolverService underTest;
    @Mock
    private PipelineExecutionRepository pipelineExecutionRepository;

    @BeforeEach
    void setUp(){
        underTest = new ExpressionResolverService(pipelineExecutionRepository);
    }

    @Test
    void canResolveStep() throws APIPYamlParsingException {

        StepParameters stepParameters = new StepParameters("step1", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                         "{\n \"q\": \"#{#jsonPath(#output.step1,'$.main')}\" \n}",
                        "#{#jsonPath(#output.step1, '$.main.msg')}"),
                2, 3000);

        HashMap<String, String> pipelineExecutionOutput = new HashMap<>();
        pipelineExecutionOutput.put("step1", "{\"main\":{\"msg\": \"step1-test-output\",\"code\": \"success\"}}");

        StepParameters expectedStepParameters = new StepParameters("step1", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                        "{\n \"q\": \"{\"msg\":\"step1-test-output\",\"code\":\"success\"}\" \n}",
                        "step1-test-output"),
                2, 3000);

        StepParameters resolvedStepParameters = underTest.resolveStep(pipelineExecutionOutput, stepParameters,
                true);

        assertThat(resolvedStepParameters).isEqualTo(expectedStepParameters);
    }

    @Test
    void willThrowWhenExpressionIsNotCorrect() {
        // expression is not closed
        StepParameters stepParameters = new StepParameters("step1", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                        "{\n \"q\": \"#{#jsonPath(#output.step1,'$.main')}\" \n}",
                        "#{#jsonPath(#output.step1, '$.main.msg')"),
                2, 3000);

        HashMap<String, String> pipelineExecutionOutput = new HashMap<>();
        pipelineExecutionOutput.put("step1", "{\"main\":{\"msg\": \"step1-test-output\",\"code\": \"success\"}}");

        assertThatThrownBy(() -> underTest.resolveStep(pipelineExecutionOutput, stepParameters, true))
                .isInstanceOf(APIPExpressionResolverException.class)
                .hasMessageContaining("Expression can not be resolved! Something wrong with the input!");
    }

    @Test
    void canGetPipelineExecutionOutput() {
        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection("pipeTest",
                LocalDateTime.now(), "prepared", new ArrayList<>(), new HashMap<>(), 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));

        HashMap<String, String> fetchedExecutionOutput = underTest.getPipelineExecutionOutput(pipelineExecutionTest.getId());

        assertThat(fetchedExecutionOutput).isEqualTo(new HashMap<>());
    }

    @Test
    void willThrowWhenExecutionNotFound(){
        String id = "pipeExeTest";

        given(pipelineExecutionRepository.findById(any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getPipelineExecutionOutput(id))
                .isInstanceOf(APIPPipelineExecutionNotFoundException.class)
                .hasMessageContaining("Could not find pipeline execution with id " + id + "!");

    }
}