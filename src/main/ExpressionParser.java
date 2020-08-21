package main;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import datamodel.Token;

public class ExpressionParser {

	private static final String pointOperator = "[\\*/]";
	private static final String dashOperator = "[\\+\\-]";
	private static final String parantheses = "[\\(\\)]";
	private static final String numeral = "(?:[0-9]+(?:\\.[0-9]{0,2})?)";
	private static final String words = asGroup(oneOf(pointOperator, dashOperator, parantheses, numeral));

	private static final Pattern pattern = Pattern.compile(words);

	private static final String valid = "^" + words + "(\\s*" + words + ")*" + "$";

	public static boolean isValid(String expression) {
		return expression.replace("(", "( ").replace(")", " )").trim().matches(valid);
	}

	private static String asGroup(String group) {
		return "(" + group + ")";
	}

	private static String oneOf(String... words) {
		return Stream.of(words).reduce((a, b) -> a + "|" + b).get();
	}

	public static List<Token> parseExpression(String expression) {

		List<Token> outp = new LinkedList<>();
		Matcher matcher = pattern.matcher(expression);
		while (matcher.find()) {
			outp.add(Token.of(matcher.group()));
		}
		return outp;
	}
}
