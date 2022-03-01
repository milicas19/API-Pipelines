package com.example.projectfirst.pipeline.apiRequestHandler;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Builder
@JsonTypeName("API_POST")
public class SpecPost implements Spec {
    String url;
    String connectorID;
    String body;
    String output;

    public SpecPost(String url, String connectorID, String body) {
        this.url = url;
        this.connectorID = connectorID;
        this.body = body;
    }
}
