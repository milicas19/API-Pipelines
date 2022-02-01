package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.ConnectorService;
import com.example.projectfirst.connector.model.Connector;
import com.example.projectfirst.connector.model.SpecKey;
import com.example.projectfirst.connector.model.SpecKeyUser;
import com.example.projectfirst.connector.model.SpecUser;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class StepService {
    @Autowired
    private ConnectorService connectorService;

    public Response executePostRequest(StepParameters stepParameters) throws IOException{
        Connector connector = connectorService.getConnectorFromYml(stepParameters.getSpec().getConnectorID());

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
        Response response = call.execute();

        return response;
    }

    public Response executeGetRequest(StepParameters stepParameters) throws IOException{
        Connector connector = connectorService.getConnectorFromYml(stepParameters.getSpec().getConnectorID());

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder().get();

        requestBuilder
                .url(stepParameters.getSpec().getUrl())
                .addHeader("host", connector.getSpec().getHost());

        addHeaderOfRequestBasedOnType(requestBuilder, connector);

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        Response response = call.execute();


        return response;
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
