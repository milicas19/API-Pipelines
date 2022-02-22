package com.example.projectfirst.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyUser {
    @Id
    private String id;
    private String name;
    private String surname;
    private String username;
    private String password;

}
