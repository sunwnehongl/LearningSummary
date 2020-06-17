package com.sun.pringsummary.spel;

import com.sun.pringsummary.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;
import java.util.Map;

/**
 * @Auther: swh
 * @Date: 2020/6/10 21:34
 * @Description:
 */
public class SpelTest {
    ExpressionParser parser;
    @Before
    public void init() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
        parser = new SpelExpressionParser(configuration);
    }

    @Test
    public void test() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("'Hello world!'");
        System.out.println(expression.getValue());
    }

    @Test
    public void testCompilerMode() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
        SpelExpressionParser parser = new SpelExpressionParser(configuration);
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        Expression expression = parser.parseExpression("name");
        System.out.println(expression.getValue(user));
    }

    @Test
    public void testList() {
        String expression = "{'a','b','c'}";
        Expression e = parser.parseExpression(expression);
        List<String> list = e.getValue(List.class);
        Expression e2 = parser.parseExpression("{1,2,3}");
        List<Integer> list2 = e2.getValue(List.class);
        System.out.println(list2);

    }

    @Test
    public void testMap() {
        String expression = "{name:'Nikola',dob:'10-July-1856'}";
        Expression e = parser.parseExpression(expression);
        Map<String,String> map = e.getValue(Map.class);
        System.out.println(map);
    }
}
