package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.ConnectorCollection;
import com.example.projectfirst.connector.ConnectorService;
import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.connector.model.Connector;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPExpressionResolverException;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StepServiceTest {

    private StepService underTest;

    @Mock
    private ConnectorService connectorService;
    @Mock
    private OkHttpClient okHttpClient;
    @Mock
    private Call remoteCall;


    @BeforeEach
    void setUp() {
        underTest = new StepService(connectorService, okHttpClient);
    }

    @Test
    void canExecutePostRequest() throws IOException {
        StepParameters stepParameters
                = new StepParameters("stepTest", "API_POST",
                new SpecPost("https://some-url", "connTest", "some-body", "some-output"),
                3, 2000);

        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: API_KEY\n" +
                "    spec: \n" +
                "        host: some-host\n" +
                "        keyHeaderName: key-header\n" +
                "        key: key";

        String id = "connTest";
        LocalDateTime dateTime = LocalDateTime.now();

        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);

        Request mockRequest = new Request.Builder()
                .url(stepParameters.getSpec().getUrl())
                .build();

        Response response =  new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{}"))
                .build();

        StepExecution expectedStepExecution = new StepExecution(200, "OK", "{}");


        given(connectorService.fetchConnector(any())).willReturn(connectorTest);
        given(okHttpClient.newCall(any())).willReturn(remoteCall);
        given(remoteCall.execute()).willReturn(response);

        StepExecution fetchedStepExecution = underTest.executePostRequest(stepParameters);

        assertThat(fetchedStepExecution).isEqualTo(expectedStepExecution);
    }

    @Test
    void willThrowWhenBodyHasUnresolvedExpression(){
        StepParameters stepParameters
                = new StepParameters("stepTest", "API_POST",
                new SpecPost("https://some-url", "connTest", "#{#jsonPath()}", "some-output"),
                3, 2000);

        assertThatThrownBy(() -> underTest.executePostRequest(stepParameters))
                .isInstanceOf(APIPExpressionResolverException.class)
                .hasMessageContaining("Expression in a body of " + stepParameters.getName() +
                        " is unresolved! Yaml file of pipeline needs to be changed!");
    }

    @Test
    void willThrowWhenExecuteOfPostRequestFails() throws IOException {
        StepParameters stepParameters
                = new StepParameters("stepTest", "API_POST",
                new SpecPost("https://some-url", "connTest", "some-body", "some-output"),
                3, 2000);

        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: API_USER\n" +
                "    spec: \n" +
                "        host: some-host\n" +
                "        userHeaderName: user-header\n" +
                "        username: username\n" +
                "        password: pass";

        String id = "connTest";
        LocalDateTime dateTime = LocalDateTime.now();

        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);

        Request mockRequest = new Request.Builder()
                .url(stepParameters.getSpec().getUrl())
                .build();

        Response response =  new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{}"))
                .build();

        given(connectorService.fetchConnector(any())).willReturn(connectorTest);
        given(okHttpClient.newCall(any())).willReturn(remoteCall);
        given(remoteCall.execute()).willThrow(IOException.class);

        assertThatThrownBy(() -> underTest.executePostRequest(stepParameters))
                .isInstanceOf(APIPStepExecutionFailedException.class)
                .isInstanceOf(IOException.class);
    }

    @Test
    void canExecuteGetRequest() throws IOException {
        StepParameters stepParameters
                = new StepParameters("stepTest", "API_GET",
                new SpecGet("https://some-url", "connTest", "some-output"),
                3, 2000);

        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: API_KEY_USER\n" +
                "    spec: \n" +
                "        host: some-host\n" +
                "        keyHeaderName: key-header\n" +
                "        key: key\n" +
                "        userHeaderName: user-header\n" +
                "        username: username\n" +
                "        password: pass";

        String id = "connTest";
        LocalDateTime dateTime = LocalDateTime.now();

        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);

        Request mockRequest = new Request.Builder()
                .url(stepParameters.getSpec().getUrl())
                .build();

        Response response =  new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{}"))
                .build();

        StepExecution expectedStepExecution = new StepExecution(200, "OK", "{}");


        given(connectorService.fetchConnector(any())).willReturn(connectorTest);
        given(okHttpClient.newCall(any())).willReturn(remoteCall);
        given(remoteCall.execute()).willReturn(response);

        StepExecution fetchedStepExecution = underTest.executeGetRequest(stepParameters);

        assertThat(fetchedStepExecution).isEqualTo(expectedStepExecution);
    }

    @Test
    void willThrowWhenExecuteOfGetRequestFails() throws IOException {
        StepParameters stepParameters
                = new StepParameters("stepTest", "API_GET",
                new SpecGet("https://some-url", "connTest", "some-output"),
                3, 2000);

        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: API_KEY_TOKEN\n" +
                "    spec: \n" +
                "        host: some-host\n" +
                "        keyHeaderName: key-header\n" +
                "        key: key\n" +
                "        token: token";

        String id = "connTest";
        LocalDateTime dateTime = LocalDateTime.now();

        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);

        Request mockRequest = new Request.Builder()
                .url(stepParameters.getSpec().getUrl())
                .build();

        Response response =  new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_1_0)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{}"))
                .build();

        given(connectorService.fetchConnector(any())).willReturn(connectorTest);
        given(okHttpClient.newCall(any())).willReturn(remoteCall);
        given(remoteCall.execute()).willThrow(IOException.class);

        assertThatThrownBy(() -> underTest.executeGetRequest(stepParameters))
                .isInstanceOf(APIPStepExecutionFailedException.class)
                .isInstanceOf(IOException.class);
    }

    @Test
    void canGetConnectorFromYml() throws APIPYamlParsingException {
        String ymlConnector = "connector:\n" +
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";

        String id = "connTest";
        String name = "Test Connector";
        String type = "NO_AUTH";
        String host = "test-host";

        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);

        given(connectorService.fetchConnector(any())).willReturn(connectorTest);

        Connector fetchedConnector = underTest.getConnectorFromYml(id);

        assertThat(fetchedConnector.getId()).isEqualTo(id);
        assertThat(fetchedConnector.getName()).isEqualTo(name);
        assertThat(fetchedConnector.getType()).isEqualTo(type);
        assertThat(fetchedConnector.getSpec().getHost()).isEqualTo(host);
    }

    @Test
    void willThrowWhenYamlIsNotCorrect(){
        // ymlConnector missing "connector:" at the beginning
        String ymlConnector =
                "    id: connTest\n" +
                "    name: Test Connector\n" +
                "    type: NO_AUTH\n" +
                "    spec: \n" +
                "        host: test-host";
        String id = "connTest";
        LocalDateTime dateTime = LocalDateTime.now();
        ConnectorCollection connectorTest = new ConnectorCollection(id, ymlConnector, dateTime, dateTime);

        given(connectorService.fetchConnector(any())).willReturn(connectorTest);

        assertThatThrownBy(() -> underTest.getConnectorFromYml(id))
                .isInstanceOf(APIPYamlParsingException.class)
                .hasMessageContaining("Error while parsing connector from yaml input!");
    }
}