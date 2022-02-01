package com.example.projectfirst.security;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class JWT {
    @Id
    private String id;
    private String jwtToken;
    public JWT(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
