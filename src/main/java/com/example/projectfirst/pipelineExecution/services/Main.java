package com.example.projectfirst.pipelineExecution.services;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class Main {
    public static String expressionResolver(ExpressionParser expressionParser,String s){
        int len = s.length();
        int p=0,k=0;
        for(int i = 0; i < len-1; i++){
            if(s.charAt(i) == '#' && s.charAt(i+1) == '{'){
                p = i+2;
                for(int j = p; j < len;j++) {
                    if (s.charAt(j) == '}') {
                        k = j;
                        break;
                    }
                }
                break;
            }
        }
        String exp = s.substring(p,k);
        Expression expression = expressionParser.parseExpression(exp);
        var expResolved = expression.getValue();
        return s.substring(0, p-2) + expResolved + s.substring(k+1);
    }


    public static void main(String[] args) throws Exception {
        ExpressionParser expressionParser = new SpelExpressionParser();
        String s = "Evaluation: #{5>23}. Should be false!";
        System.out.println(expressionResolver(expressionParser, s));
    }
}
