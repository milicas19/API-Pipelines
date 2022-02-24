package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.ResolvedExpression;
import com.example.projectfirst.pipelineExecution.exception.APIPExpressionResolverException;
import com.google.gson.Gson;
import com.jayway.jsonpath.internal.ParseContextImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.util.LinkedHashMap;

@Slf4j
public class ExpressionResolverUtil {

    public static <T> T evaluate(String json, String jsonPath){
        return (new ParseContextImpl()).parse(json).read(jsonPath);
    }

    public static ResolvedExpression findExpressionAndReplace(String yamlOfStep,
                                                              int startIndex,
                                                              StandardEvaluationContext context,
                                                              boolean beforeExecution)
            throws APIPExpressionResolverException {

        int len = yamlOfStep.length();
        int beginIndex = -1;
        int endIndex = -1;
        for (int i = startIndex; i < len - 1; i++) {
            if (yamlOfStep.charAt(i) == '#' && yamlOfStep.charAt(i + 1) == '{') {
                beginIndex = i + 2;
                for (int j = beginIndex; j < len; j++) {
                    if (yamlOfStep.charAt(j) == '}') {
                        endIndex = j;
                        break;
                    }
                }
                break;
            }
        }

        if(beginIndex == -1)
            return null;

        if(endIndex == -1)
            throw new APIPExpressionResolverException("Expression can not be resolved! Something wrong with the input!");

        ResolvedExpression resolvedExpressionInString = new ResolvedExpression(yamlOfStep, endIndex + 1);

        String expressionString = yamlOfStep.substring(beginIndex, endIndex);
        log.info("expression string: " + expressionString);

        try {
            ExpressionParser expressionParser = new SpelExpressionParser();

            Expression expression = expressionParser.parseExpression(expressionString);
            var resolvedExpression = expression.getValue(context);
            String resolvedExpressionString = resolvedExpression.toString();

            // if resolvedExpression is json
            if(resolvedExpression.getClass().equals(LinkedHashMap.class)){
                Gson gson = new Gson();
                resolvedExpressionString = gson.toJson(resolvedExpression, LinkedHashMap.class);
                resolvedExpressionString = resolvedExpressionString.replace("\"", "\\\"");
            }

            log.info("resolved expression string: " + resolvedExpressionString);

            yamlOfStep = replaceExpressionWithResolvedExpression(yamlOfStep, expressionString, resolvedExpressionString);
            startIndex = beginIndex - 2 + resolvedExpressionString.length();

            log.info("start index for next expr: " + startIndex);

            log.info("ymlOfStep: "+ yamlOfStep);
            resolvedExpressionInString.setStringWithResolvedExpression(yamlOfStep);
            resolvedExpressionInString.setStartIndexOfSearchForNextExpression(startIndex);
            return resolvedExpressionInString;
        } catch (SpelEvaluationException e) {
            if (beforeExecution && e.getMessage().startsWith("EL1008E")) {
                log.info(e.getMessage());
                return resolvedExpressionInString;
            }
            throw new APIPExpressionResolverException("Something wrong with evaluation of expression: " + expressionString);
        } catch (SpelParseException ex) {
            throw new APIPExpressionResolverException("Could not parse expression: " + expressionString);
        }
    }

    public static String replaceExpressionWithResolvedExpression(String s,
                                                               String expression,
                                                               String resolvedExpression){
        return s.replace("#{" + expression +"}", resolvedExpression);
    }
}
