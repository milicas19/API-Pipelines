package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.ResolvedExpression;
import com.example.projectfirst.pipelineExecution.exception.APIPExpressionResolverException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
class ExpressionResolverUtilTest {

    @Test
    void canFindExpressionAndReplace() {
        String stringWithExpression
                = "This is #{some-expression} with #{some-expression} and it needs to be replaced with #{other-expression}";
        String expression = "some-expression";
        String resolvedExpression = "resolved-expression";
        String expectedStringWithResolvedExpression
                = "This is " + resolvedExpression +  " with " + resolvedExpression +
                " and it needs to be replaced with #{other-expression}";

        String stringWithResolvedExpression = ExpressionResolverUtil.replaceExpressionWithResolvedExpression(stringWithExpression, expression, resolvedExpression);

        assertThat(stringWithResolvedExpression).isEqualTo(expectedStringWithResolvedExpression);
    }

    @Test
    void canReplaceExpressionWithResolvedExpression() {
        String yamlOfStep =
                "name: step2\n" +
                "        type: API_POST\n" +
                "        spec:\n" +
                "            url: \"https://some-url\"\n" +
                "            connectorID: \"21\"\n" +
                "            body: |\n" +
                "                {\n" +
                "                    \"key\": \"#{#jsonPath(#output.step1,'$.main')}\"\n" +
                "                }\n" +
                "        retry: 3\n" +
                "        backOffPeriod: 2000";

        int startIndex = 0;

        StandardEvaluationContext context = new StandardEvaluationContext();
        Method method = BeanUtils.resolveSignature("evaluate", ExpressionResolverUtil.class);
        context.registerFunction("jsonPath", method);
        HashMap<String, String> pipelineExecutionOutput = new HashMap<>();
        pipelineExecutionOutput.put("step1", "{\"main\":{\"msg\": \"step1-test-output\",\"code\": \"success\"}}");
        context.setVariable("output",pipelineExecutionOutput);
        context.addPropertyAccessor(new MapAccessor());

        String expectedResolvedYamlOfStep =
                "name: step2\n" +
                "        type: API_POST\n" +
                "        spec:\n" +
                "            url: \"https://some-url\"\n" +
                "            connectorID: \"21\"\n" +
                "            body: |\n" +
                "                {\n" +
                "                    \"key\": \"{\\\"msg\\\":\\\"step1-test-output\\\",\\\"code\\\":\\\"success\\\"}\"\n" +
                "                }\n" +
                "        retry: 3\n" +
                "        backOffPeriod: 2000";

        String lastExprResolved = "{\\\"msg\\\":\\\"step1-test-output\\\",\\\"code\\\":\\\"success\\\"}";
        int expectedStartIndexOfSearchForNextExpression = expectedResolvedYamlOfStep.indexOf(lastExprResolved)
                + lastExprResolved.length();

        ResolvedExpression resolvedYamlOfStepWithExpr
                = ExpressionResolverUtil.findExpressionAndReplace(yamlOfStep, startIndex, context, true);
        ResolvedExpression resolvedExpressionOfStepWithoutExpr
                = ExpressionResolverUtil.findExpressionAndReplace(yamlOfStep, expectedStartIndexOfSearchForNextExpression,
                context, true);

        assert resolvedYamlOfStepWithExpr != null;
        assertThat(resolvedYamlOfStepWithExpr.getStringWithResolvedExpression()).isEqualTo(expectedResolvedYamlOfStep);
        assertThat(resolvedYamlOfStepWithExpr.getStartIndexOfSearchForNextExpression()).isEqualTo(expectedStartIndexOfSearchForNextExpression);
        assertThat(resolvedExpressionOfStepWithoutExpr).isEqualTo(null);

    }

    @Test
    void willThrowWhenExpressionNotClosed(){
        String yamlOfStep =
                "name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://some-url\"\n" +
                "            connectorID: \"21\"\n" +
                "            output: \"#{'some-'.concat('expression')\"\n" +
                "        retry: 3\n" +
                "        backOffPeriod: 2000";

        assertThatThrownBy(()
                -> ExpressionResolverUtil.findExpressionAndReplace(yamlOfStep, 0, new StandardEvaluationContext(),true))
                .isInstanceOf(APIPExpressionResolverException.class)
                .hasMessageContaining("Expression is not closed!");

    }

    @Test
    void willThrowWhenSpelCanNotEvaluateExpression(){
        // output does not contain key 'step'
        String yamlOfStep =
                "name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://some-url\"\n" +
                "            connectorID: \"21\"\n" +
                "            output: \"#{#jsonPath(#output.step,'$.main')}\"\n" +
                "        retry: 3\n" +
                "        backOffPeriod: 2000";

        String expr = "#{#jsonPath(#output.step,'$.main')}";
        String exprString = "#jsonPath(#output.step,'$.main')";

        StandardEvaluationContext context = new StandardEvaluationContext();
        Method method = BeanUtils.resolveSignature("evaluate", ExpressionResolverUtil.class);
        context.registerFunction("jsonPath", method);
        HashMap<String, String> pipelineExecutionOutput = new HashMap<>();
        pipelineExecutionOutput.put("step1", "{\"main\":{\"msg\": \"step1-test-output\",\"code\": \"success\"}}");
        context.setVariable("output",pipelineExecutionOutput);
        context.addPropertyAccessor(new MapAccessor());

        ResolvedExpression resolvedExpression
                = ExpressionResolverUtil.findExpressionAndReplace(yamlOfStep,0,context,true);

        ResolvedExpression expectedResolvedExpression = new ResolvedExpression(yamlOfStep, yamlOfStep.indexOf(expr) + expr.length());

        assertThat(resolvedExpression).isEqualTo(expectedResolvedExpression);
        assertThatThrownBy(()
                -> ExpressionResolverUtil.findExpressionAndReplace(yamlOfStep, 0, context,false))
                .isInstanceOf(APIPExpressionResolverException.class)
                .hasMessageContaining("Something wrong with evaluation of expression: " + exprString);
    }

    @Test
    void willThrowWhenExpressionNotCorrect() {
        // missing ')' at the end of expression
        String yamlOfStep = "name: step1\n" +
                "        type: API_GET\n" +
                "        spec:\n" +
                "            url: \"https://some-url\"\n" +
                "            connectorID: \"21\"\n" +
                "            output: \"#{#jsonPath(#output.step1,'$.main'}\"\n" +
                "        retry: 3\n" +
                "        backOffPeriod: 2000";

        String expr = "#{#jsonPath(#output.step1,'$.main'}";
        String exprString = "#jsonPath(#output.step1,'$.main'";

        StandardEvaluationContext context = new StandardEvaluationContext();
        Method method = BeanUtils.resolveSignature("evaluate", ExpressionResolverUtil.class);
        context.registerFunction("jsonPath", method);
        HashMap<String, String> pipelineExecutionOutput = new HashMap<>();
        pipelineExecutionOutput.put("step1", "{\"main\":{\"msg\": \"step1-test-output\",\"code\": \"success\"}}");
        context.setVariable("output", pipelineExecutionOutput);
        context.addPropertyAccessor(new MapAccessor());

        assertThatThrownBy(()
                -> ExpressionResolverUtil.findExpressionAndReplace(yamlOfStep, 0, context, true))
                .isInstanceOf(APIPExpressionResolverException.class)
                .hasMessageContaining("Could not parse expression: " + exprString);
    }
}