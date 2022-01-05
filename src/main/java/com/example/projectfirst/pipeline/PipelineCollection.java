package com.example.projectfirst.pipeline;

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
public class PipelineCollection {
    @Id
    private String id;
    private String ymlFile;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    public PipelineCollection(String ymlFile, LocalDateTime creationDate, LocalDateTime modificationDate) {
        this.ymlFile = ymlFile;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }
}
