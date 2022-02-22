package com.example.projectfirst.pipelineExecution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolvedExpression {
    String stringWithResolvedExpression;
    int startIndexOfSearchForNextExpression;
}
