package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.exception.APIPExpressionResolverException;
import com.jayway.jsonpath.internal.ParseContextImpl;

public class ExpressionResolverUtil {

    public static <T> T evaluate(String json, String jsonPath){
        return new ParseContextImpl().parse(json).read(jsonPath);
    }

    public static String findExpression(String s) {
        int len = s.length();
        int beginIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < len - 1; i++) {
            if (s.charAt(i) == '#' && s.charAt(i + 1) == '{') {
                beginIndex = i + 2;
                for (int j = beginIndex; j < len; j++) {
                    if (s.charAt(j) == '}') {
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
            throw new APIPExpressionResolverException("Expression can not be resolved!Something wrong with the input!");

        return  s.substring(beginIndex, endIndex);
    }

    public static String replaceExpressionWithResolvedExpression(String s,
                                                               String expression,
                                                               String resolvedExpression){
        return s.replace("#{" + expression +"}", resolvedExpression);
    }
}
