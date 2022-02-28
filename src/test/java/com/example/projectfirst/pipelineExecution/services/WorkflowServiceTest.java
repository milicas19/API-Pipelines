package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.pipeline.PipelineCollection;
import com.example.projectfirst.pipeline.PipelineService;
import com.example.projectfirst.pipeline.apiRequestHandler.SpecGet;
import com.example.projectfirst.pipeline.apiRequestHandler.SpecPost;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.StatusOfStepExecution;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPInitiateExecutionFailed;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionFailedException;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionNotFoundException;
import com.example.projectfirst.pipelineExecution.exception.APIPRetryMechanismException;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    private WorkflowService underTest;
    @Mock
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Mock
    private PipelineService pipelineService;
    @Mock
    private ExpressionResolverService expressionResolverService;
    @Mock
    private ExecutionService executionService;
    @Mock
    private StateService stateService;
    @Mock
    private SaveOutputService saveOutputService;

    @BeforeEach
    void setUp() {
        underTest = new WorkflowService(pipelineExecutionRepository, pipelineService, expressionResolverService,
                executionService, stateService, saveOutputService);
    }

    @Test
    void canInitiateExecution() throws APIPInitiateExecutionFailed {
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
                "            connectorID: \"connID\"\n" +
                "            output: \"#{some-expr}\"";
        LocalDateTime dateTime = LocalDateTime.now();
        String pipeId = "pipeTest";
        String expectedPipelineExeId = "pipeExeTest";
        PipelineCollection pipelineTest = new PipelineCollection(pipeId, ymlPipeline, dateTime, dateTime);

        List<StepParameters> steps = new ArrayList<>();
        steps.add(new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{some-expr}"),
                2, 3000));

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(expectedPipelineExeId,
                pipeId, dateTime,"prepared", steps, new HashMap<>(), 0);

        given(pipelineService.fetchPipeline(any())).willReturn(pipelineTest);
        given(pipelineExecutionRepository.save(any())).willReturn(pipelineExecutionTest);

        String pipelineExeId = underTest.initiateExecution(pipeId);

        assertThat(pipelineExeId).isEqualTo(expectedPipelineExeId);
    }

    @Test
    void willThrowWhenYamlOfPipelineIsNotCorrect() {
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
                "            connectorID: \"connID\"\n" +
                "            output: \"#{some-expr}\"";
        LocalDateTime dateTime = LocalDateTime.now();
        String id = "pipeTest";
        PipelineCollection pipelineTest = new PipelineCollection(id, ymlPipeline, dateTime, dateTime);

        given(pipelineService.fetchPipeline(any())).willReturn(pipelineTest);

        assertThatThrownBy(() -> underTest.initiateExecution(id))
                .isInstanceOf(IOException.class);
    }

    @Test
    void canExecutePipelineSteps() throws APIPYamlParsingException, APIPStepExecutionFailedException, APIPRetryMechanismException {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        HashMap<String, String> output = new HashMap<>();
        HashMap<String, String> outputAfterFirstStep = new HashMap<>();
        outputAfterFirstStep.put("step1", "step1-test-output");

        LocalDateTime dateTime = LocalDateTime.now();

        List<StepParameters> stepsBefore = new ArrayList<>();
        List<StepParameters> stepsAfter = new ArrayList<>();
        StepParameters firstStepParameters = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        StepParameters resolvedFirstStepParameter = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        StepParameters secondStepParameters = new StepParameters("step2", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$')}"),
                2, 3000);
        StepParameters resolvedSecondStepParameter = new StepParameters("step2", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        stepsBefore.add(firstStepParameters);
        stepsBefore.add(secondStepParameters);
        stepsAfter.add(resolvedFirstStepParameter);
        stepsAfter.add(resolvedSecondStepParameter);

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime, "prepared", stepsBefore, output, 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn("prepared", "running");
        given(expressionResolverService.resolveStep(any(),any(),anyBoolean())).willReturn(stepsBefore.get(0),
                stepsAfter.get(0), stepsAfter.get(1), stepsAfter.get(1));

        pipelineExecutionTest.setState("running");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("running"))).willReturn(pipelineExecutionTest);

        StepExecution firstStepExecution
                = new StepExecution(StatusOfStepExecution.SUCCESS,
                "{\"msg\":\"step1-test-output\",\"code\":\"success\"}");
        StepExecution secondStepExecution
                = new StepExecution(StatusOfStepExecution.SUCCESS, "step1-test-output");
        given(executionService.executeStep(any())).willReturn(firstStepExecution, secondStepExecution);

        outputAfterFirstStep.put("step1", "step1-test-output");
        pipelineExecutionTest.setOutput(outputAfterFirstStep);
        pipelineExecutionTest.setState("finished");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("finished"))).willReturn(pipelineExecutionTest);

        PipelineExecutionCollection pipelineExecution = underTest.executePipelineSteps(pipelineExecutionTestId);

        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> responseArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> stepNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(saveOutputService, times(2)).save(idArgumentCaptor.capture(), responseArgumentCaptor.capture(), stepNameArgumentCaptor.capture());

        List<String> capturedPipelineExeIds = idArgumentCaptor.getAllValues();
        List<String> capturedStepResponses = responseArgumentCaptor.getAllValues();
        List<String> capturedStepNames = stepNameArgumentCaptor.getAllValues();

        AssertionsForClassTypes.assertThat(capturedPipelineExeIds.get(0)).isEqualTo(pipelineExecutionTestId);
        AssertionsForClassTypes.assertThat(capturedStepResponses.get(0)).isEqualTo(stepsAfter.get(0).getSpec().getOutput());
        AssertionsForClassTypes.assertThat(capturedStepNames.get(0)).isEqualTo(stepsAfter.get(0).getName());

        AssertionsForClassTypes.assertThat(capturedPipelineExeIds.get(1)).isEqualTo(pipelineExecutionTestId);
        AssertionsForClassTypes.assertThat(capturedStepResponses.get(1)).isEqualTo(secondStepExecution.getOutput());
        AssertionsForClassTypes.assertThat(capturedStepNames.get(1)).isEqualTo(stepsAfter.get(1).getName());

        assertThat(pipelineExecution).isEqualTo(pipelineExecutionTest);
    }

    @Test
    void canExecutePipelineStepsOfPausedPipelineExecution() throws APIPYamlParsingException, APIPStepExecutionFailedException, APIPRetryMechanismException {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        HashMap<String, String> outputAfterFirstStep = new HashMap<>();
        outputAfterFirstStep.put("step1", "step1-test-output");
        LocalDateTime dateTime = LocalDateTime.now();

        List<StepParameters> stepsBefore = new ArrayList<>();
        List<StepParameters> stepsAfter = new ArrayList<>();
        StepParameters firstStepParameters = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        StepParameters resolvedFirstStepParameter = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        StepParameters secondStepParameters = new StepParameters("step2", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$')}"),
                2, 3000);
        StepParameters resolvedSecondStepParameter = new StepParameters("step2", "API_POST",
                new SpecPost("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        stepsBefore.add(firstStepParameters);
        stepsBefore.add(secondStepParameters);
        stepsAfter.add(resolvedFirstStepParameter);
        stepsAfter.add(resolvedSecondStepParameter);

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime,"paused", stepsBefore, outputAfterFirstStep, 1);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn("running");
        given(expressionResolverService.getPipelineExecutionOutput(any())).willReturn(outputAfterFirstStep);
        given(expressionResolverService.resolveStep(any(),any(),anyBoolean())).willReturn(stepsBefore.get(1),
                stepsAfter.get(1));

        pipelineExecutionTest.setState("running");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("running"))).willReturn(pipelineExecutionTest);

        StepExecution secondStepExecution
                = new StepExecution(StatusOfStepExecution.SUCCESS, "step1-test-output");
        given(executionService.executeStep(any())).willReturn(secondStepExecution);

        outputAfterFirstStep.put("step2", "step1-test-output");
        pipelineExecutionTest.setOutput(outputAfterFirstStep);
        pipelineExecutionTest.setSteps(stepsAfter);
        given(stateService.setState(eq(pipelineExecutionTestId), eq("finished"))).willReturn(pipelineExecutionTest);

        PipelineExecutionCollection pipelineExecution = underTest.executePipelineSteps(pipelineExecutionTestId);

        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> responseArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> stepNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(saveOutputService).save(idArgumentCaptor.capture(), responseArgumentCaptor.capture(), stepNameArgumentCaptor.capture());

        String capturedPipelineExeId = idArgumentCaptor.getValue();
        String capturedStepResponse = responseArgumentCaptor.getValue();
        String capturedStepName = stepNameArgumentCaptor.getValue();

        assertThat(capturedPipelineExeId).isEqualTo(pipelineExecutionTestId);
        assertThat(capturedStepResponse).isEqualTo(secondStepExecution.getOutput());
        assertThat(capturedStepName).isEqualTo(stepsAfter.get(1).getName());

        assertThat(pipelineExecution).isEqualTo(pipelineExecutionTest);
    }

    @Test
    void willThrowWhenExpressionInOutputIsNotResolved() throws APIPYamlParsingException, APIPStepExecutionFailedException, APIPRetryMechanismException {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        HashMap<String, String> output = new HashMap<>();
        HashMap<String, String> outputAfterFirstStep = new HashMap<>();
        outputAfterFirstStep.put("step1", "step1-test-output");

        LocalDateTime dateTime = LocalDateTime.now();

        List<StepParameters> stepsBefore = new ArrayList<>();
        List<StepParameters> stepsAfter = new ArrayList<>();
        StepParameters stepParameters = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        StepParameters resolvedStepParameter = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        stepsBefore.add(stepParameters);
        stepsAfter.add(resolvedStepParameter);

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime, "prepared", stepsBefore, output, 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn("prepared");
        given(expressionResolverService.resolveStep(any(),any(),anyBoolean())).willReturn(stepsBefore.get(0),
                stepsAfter.get(0));

        pipelineExecutionTest.setState("running");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("running"))).willReturn(pipelineExecutionTest);

        StepExecution stepExecution
                = new StepExecution(StatusOfStepExecution.SUCCESS, "");
        given(executionService.executeStep(any())).willReturn(stepExecution);

        assertThatThrownBy(() -> underTest.executePipelineSteps(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineExecutionFailedException.class)
                .hasMessageContaining("Pipeline execution failed: "
                        + stepParameters.getName() + " failed! Unresolved expression in output!");
    }

    @Test
    void willThrowWhenStatusOfStepExeIsFailure() throws APIPYamlParsingException, APIPStepExecutionFailedException {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        HashMap<String, String> outputAfterFirstStep = new HashMap<>();
        outputAfterFirstStep.put("step1", "step1-test-output");

        LocalDateTime dateTime = LocalDateTime.now();

        List<StepParameters> stepsBefore = new ArrayList<>();
        List<StepParameters> stepsAfter = new ArrayList<>();
        StepParameters stepParameters = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        StepParameters resolvedStepParameter = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        stepsBefore.add(stepParameters);
        stepsAfter.add(resolvedStepParameter);


        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime,"prepared", stepsBefore, outputAfterFirstStep, 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn("running");
        given(expressionResolverService.getPipelineExecutionOutput(any())).willReturn(outputAfterFirstStep);
        given(expressionResolverService.resolveStep(any(),any(),anyBoolean())).willReturn(stepsBefore.get(0),
                stepsAfter.get(0));

        pipelineExecutionTest.setState("running");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("running"))).willReturn(pipelineExecutionTest);

        StepExecution stepExecution
                = new StepExecution(StatusOfStepExecution.FAILURE, "");
        given(executionService.executeStep(any())).willReturn(stepExecution);

        assertThatThrownBy(() -> underTest.executePipelineSteps(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineExecutionFailedException.class)
                .hasMessageContaining("Pipeline execution failed: " + stepParameters.getName() + " failed!");
    }

    @Test
    void willThrowWhenStepExecutionFails() throws APIPYamlParsingException, APIPStepExecutionFailedException {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        HashMap<String, String> outputAfterFirstStep = new HashMap<>();
        outputAfterFirstStep.put("step1", "step1-test-output");

        LocalDateTime dateTime = LocalDateTime.now();

        List<StepParameters> stepsBefore = new ArrayList<>();
        List<StepParameters> stepsAfter = new ArrayList<>();
        StepParameters stepParameters = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        StepParameters resolvedStepParameter = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        stepsBefore.add(stepParameters);
        stepsAfter.add(resolvedStepParameter);


        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime,"prepared", stepsBefore, outputAfterFirstStep, 0);


        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn("running");
        given(expressionResolverService.getPipelineExecutionOutput(any())).willReturn(outputAfterFirstStep);
        given(expressionResolverService.resolveStep(any(),any(),anyBoolean())).willReturn(stepsBefore.get(0),
                stepsAfter.get(0));

        pipelineExecutionTest.setState("running");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("running")))
                .willReturn(pipelineExecutionTest);

        pipelineExecutionTest.setState("aborted");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("aborted")))
                .willReturn(pipelineExecutionTest);
        given(executionService.executeStep(any())).willThrow(APIPStepExecutionFailedException.class);

        assertThatThrownBy(() -> underTest.executePipelineSteps(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineExecutionFailedException.class)
                .hasMessageContaining("Pipeline execution failed: " + stepParameters.getName() + " failed!");
    }

    @Test
    void willThrowWhenYamlOfConnectorIsNotCorrect() throws APIPYamlParsingException, APIPStepExecutionFailedException {
        String pipelineId = "pipeTest";
        String pipelineExecutionTestId = "pipeExeTest";

        HashMap<String, String> outputAfterFirstStep = new HashMap<>();
        outputAfterFirstStep.put("step1", "step1-test-output");

        LocalDateTime dateTime = LocalDateTime.now();

        List<StepParameters> stepsBefore = new ArrayList<>();
        List<StepParameters> stepsAfter = new ArrayList<>();
        StepParameters stepParameters = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{#jsonPath(output.step1, '$.msg')}"),
                2, 3000);
        StepParameters resolvedStepParameter = new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "step1-test-output"),
                2, 3000);
        stepsBefore.add(stepParameters);
        stepsAfter.add(resolvedStepParameter);


        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineExecutionTestId,
                pipelineId, dateTime,"prepared", stepsBefore, outputAfterFirstStep, 0);

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn("running");
        given(expressionResolverService.getPipelineExecutionOutput(any())).willReturn(outputAfterFirstStep);
        given(expressionResolverService.resolveStep(any(),any(),anyBoolean())).willReturn(stepsBefore.get(0),
                stepsAfter.get(0));

        pipelineExecutionTest.setState("running");
        given(stateService.setState(eq(pipelineExecutionTestId), eq("running"))).willReturn(pipelineExecutionTest);

        given(executionService.executeStep(any())).willThrow(APIPYamlParsingException.class);

        assertThatThrownBy(() -> underTest.executePipelineSteps(pipelineExecutionTestId))
                .isInstanceOf(APIPYamlParsingException.class);
    }

    @Test
    void willThrowWhenPipelineExecutionNotFound() {
        String pipelineExecutionTestId = "pipeExeId";

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.executePipelineSteps(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineExecutionNotFoundException.class)
                .hasMessageContaining("Could not find pipeline execution with id " + pipelineExecutionTestId + "!");

    }

    @Test
    void willThrowWhenPipelineIsPausedOrAborted() {
        String pipelineId = "pipeTest";
        String state = "paused";

        List<StepParameters> steps = new ArrayList<>();
        steps.add(new StepParameters("step1", "API_GET",
                new SpecGet("https://some-url",
                        "connID",
                        "#{some-expr}"),
                2, 3000));

        HashMap<String, String> output = new HashMap<>();
        LocalDateTime dateTime = LocalDateTime.now();

        PipelineExecutionCollection pipelineExecutionTest = new PipelineExecutionCollection(pipelineId, dateTime,
                state, steps, output, 0);
        String pipelineExecutionTestId = pipelineExecutionTest.getId();

        given(pipelineExecutionRepository.findById(any())).willReturn(Optional.of(pipelineExecutionTest));
        given(stateService.checkState(any())).willReturn(state);

        assertThatThrownBy(() -> underTest.executePipelineSteps(pipelineExecutionTestId))
                .isInstanceOf(APIPPipelineExecutionFailedException.class)
                .hasMessageContaining("Pipeline execution " + state + "!");

    }
}