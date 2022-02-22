package com.example.projectfirst.pipelineExecution.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExpressionResolverUtilTest {

    @Test
    void canFindExpressionAndReplace() {
        String stringWithExpression
                = "This is #{some-expression} with #{some-expression} and it needs to be replaced with #{other-expression}";
        String expression = "some-expression";
        String resolvedExpression = "resolved-expression";
        String expectedStringWithResolvedExpression
                = "This is resolved-expression with resolved-expression and it needs to be replaced with #{other-expression}";

        String stringWithResolvedExpression = ExpressionResolverUtil.replaceExpressionWithResolvedExpression(stringWithExpression, expression, resolvedExpression);

        assertThat(stringWithResolvedExpression).isEqualTo(expectedStringWithResolvedExpression);
    }

    @Disabled
    @Test
    void replaceExpressionWithResolvedExpression() {
    }
}