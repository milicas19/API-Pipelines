package com.example.projectfirst.connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorCollection {
    @Id
    private String id;
    private String ymlFile;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
