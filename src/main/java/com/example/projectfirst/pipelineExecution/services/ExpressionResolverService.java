package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.ResolvedExpression;
import com.example.projectfirst.pipelineExecution.exception.APIPExpressionResolverException;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ExpressionResolverService {

    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public StepParameters resolveStep(Map<String, String> pipelineExecutionOutput,
                                      StepParameters stepParameters, boolean beforeExecution) throws APIPYamlParsingException {

        log.info("Resolving expressions for " + stepParameters.getName()+ "!");

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            String yaml = mapper.writeValueAsString(stepParameters);

            StandardEvaluationContext context = new StandardEvaluationContext();
            Method method = BeanUtils.resolveSignature("evaluate", ExpressionResolverUtil.class);


            context.registerFunction("jsonPath", method);
            context.setVariable("output", pipelineExecutionOutput);
            context.addPropertyAccessor(new MapAccessor());

            int startIndex = 0;
            while (true) {
                try {
                    ResolvedExpression resolvedExpression
                            = ExpressionResolverUtil.findExpressionAndReplace(yaml, startIndex, context, beforeExecution);
                    if (resolvedExpression == null)
                        break;
                    yaml = resolvedExpression.getStringWithResolvedExpression();
                    startIndex = resolvedExpression.getStartIndexOfSearchForNextExpression();
                } catch (APIPExpressionResolverException e) {
                    throw new APIPExpressionResolverException(e.getMessage());
                }
            }
            log.info(yaml);
            return mapper.readValue(yaml, StepParameters.class);
        }catch (IOException e) {
            log.error("Error while parsing step parameters to/from yaml input! Message: " + e.getMessage());
            throw new APIPYamlParsingException("Error while parsing step parameters to/from yaml input!");
        }
    }

    public HashMap<String, String> getPipelineExecutionOutput(String pipelineExeId) {
        return pipelineExecutionRepository.findById(pipelineExeId)
                .map(PipelineExecutionCollection::getOutput)
                .orElseThrow(() -> new APIPPipelineExecutionNotFoundException("Could not find pipeline execution with id " + pipelineExeId + "!"));
    }
}
