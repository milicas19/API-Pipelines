package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.*;
import com.example.projectfirst.connector.model.Connector;
import com.example.projectfirst.connector.model.SpecKey;
import com.example.projectfirst.connector.model.SpecKeyUser;
import com.example.projectfirst.connector.model.SpecUser;
import com.example.projectfirst.pipeline.model.SpecPost;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.Map;

public class PostHandler implements StepHandler {

    @Override
    public Response execute(StepParameters stepParameters, ConnectorService connectorService) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String connectorYml = connectorService.fetchConnector(stepParameters.getSpec().getConnectorID()).getYmlFile();
        Map<String, Connector> connectorMap = objectMapper.readValue(connectorYml,
                new TypeReference<>(){});
        Connector connector = connectorMap.get("connector");

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,((SpecPost)stepParameters.getSpec()).getBody());

        Request.Builder requestBuilder = new Request.Builder().get();
        requestBuilder
                .url(stepParameters.getSpec().getUrl())
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("host", connector.getSpec().getHost());

        switch (connector.getType()){
            case "API_KEY":
                requestBuilder.addHeader(((SpecKey) connector.getSpec()).getKeyHeaderName(),
                        ((SpecKey) connector.getSpec()).getKey());
                break;
            case "API_USER":
                requestBuilder.addHeader(((SpecUser)connector.getSpec()).getUserHeaderName(),Credentials.basic(((SpecUser)connector.getSpec()).getUsername(),
                        ((SpecUser)connector.getSpec()).getPassword()));
                break;
            case "API_USER_KEY":
                requestBuilder
                        .addHeader(((SpecKeyUser) connector.getSpec()).getKeyHeaderName(),
                                ((SpecKeyUser) connector.getSpec()).getKey())
                        .addHeader(((SpecKeyUser)connector.getSpec()).getUserHeaderName(),Credentials.basic(((SpecKeyUser)connector.getSpec()).getUsername(),
                                ((SpecKeyUser)connector.getSpec()).getPassword()));
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        Response response = call.execute();

        return response;

    }
}
