package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.ConnectorService;
import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.connector.model.Connector;
import com.example.projectfirst.connector.model.SpecKey;
import com.example.projectfirst.connector.model.SpecKeyUser;
import com.example.projectfirst.connector.model.SpecUser;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class StepService {
    @Autowired
    private ConnectorService connectorService;

    public StepExecution executePostRequest(StepParameters stepParameters)
            throws APIPYamlParsingException, APIPStepExecutionFailedException {

        log.info("Execution of post request!");

        Connector connector = getConnectorFromYml(stepParameters.getSpec().getConnectorID());

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder().get();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,((SpecPost)stepParameters.getSpec()).getBody());

        requestBuilder
                .url(stepParameters.getSpec().getUrl())
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("host", connector.getSpec().getHost());

        addHeaderOfRequestBasedOnType(requestBuilder, connector);

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return new StepExecution(response.code(), response.message(), response.body().string());
        }catch (IOException e){
            throw new APIPStepExecutionFailedException(e);
        }
    }

    public StepExecution executeGetRequest(StepParameters stepParameters)
            throws APIPYamlParsingException, APIPStepExecutionFailedException{

        log.info("Execution of get request!");

        Connector connector = getConnectorFromYml(stepParameters.getSpec().getConnectorID());

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder().get();

        requestBuilder
                .url(stepParameters.getSpec().getUrl())
                .addHeader("host", connector.getSpec().getHost());

        addHeaderOfRequestBasedOnType(requestBuilder, connector);

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return new StepExecution(response.code(), response.message(), response.body().string());
        }catch (IOException e){
            throw new APIPStepExecutionFailedException(e);
        }
    }

    public Connector getConnectorFromYml(String id) throws APIPYamlParsingException{
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String connectorYml = connectorService.fetchConnector(id).getYmlFile();
        try {
            Map<String, Connector> connectorMap = objectMapper.readValue(connectorYml, new TypeReference<>() {
            });
            return connectorMap.get("connector");
        }catch (IOException e){
            throw new APIPYamlParsingException("Error while parsing connector from yaml input!");
        }
    }

    public void addHeaderOfRequestBasedOnType(Request.Builder requestBuilder, Connector connector){
        switch (connector.getType()){
            case "API_KEY":
                requestBuilder.addHeader(((SpecKey) connector.getSpec()).getKeyHeaderName(),
                        ((SpecKey) connector.getSpec()).getKey());
                break;
            case "API_USER":
                requestBuilder.addHeader(((SpecUser)connector.getSpec()).getUserHeaderName(),
                        Credentials.basic(((SpecUser)connector.getSpec()).getUsername(),
                                ((SpecUser)connector.getSpec()).getPassword()));
                break;
            case "API_USER_KEY":
                requestBuilder
                        .addHeader(((SpecKeyUser) connector.getSpec()).getKeyHeaderName(),
                                ((SpecKeyUser) connector.getSpec()).getKey())
                        .addHeader(((SpecKeyUser)connector.getSpec()).getUserHeaderName(),
                                Credentials.basic(((SpecKeyUser)connector.getSpec()).getUsername(),
                                        ((SpecKeyUser)connector.getSpec()).getPassword()));
        }
    }

}
