package main;

import java.math.BigDecimal;
import java.util.List;

import datamodel.Token;

public class Calculator {

	public static BigDecimal calculate(String expression) {

		if (!ExpressionParser.isValid(expression)) {
			throw new IllegalArgumentException("Expression '" + expression + "' is not a valid expression.");
		}
		List<Token> tokenList = ExpressionParser.parseExpression(expression);
		TokenParser.parse(tokenList);
		return calculate(tokenList);

	}

	private static BigDecimal calculate(List<Token> tokenList) {
		if (tokenList.size() != 1) {
			throw new IllegalArgumentException("Token list must have size 1, but has size " + tokenList.size() + ".");
		}
		return tokenList.get(0).calculateValue();
	}

}
