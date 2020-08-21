package main;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import datamodel.BinaryOperationToken;
import datamodel.ExpressionToken;
import datamodel.SignToken;
import datamodel.Token;
import lombok.NonNull;

public class TokenParser {

	private static final Token TOKEN_CLOSE_PARANTHESIS = Token.of(")");
	private static final Token TOKEN_ADDITION_OPERATOR = Token.of("+");
	private static final Token TOKEN_SUBTRACTION_OPERATOR = Token.of("-");
	private static final Token TOKEN_MULTIPLICATION_OPERATOR = Token.of("*");
	private static final Token TOKEN_DIVISION_OPERATOR = Token.of("/");

	public static void parse(List<Token> tokenList) {
		resolveParantheses(tokenList);
		resolveSigns(tokenList);
		resolvePointOperations(tokenList);
		resolveDashOperations(tokenList);
	}

	private static void recursivelyCall(List<Token> tokenList, Consumer<List<Token>> thisLevel) {
		recursivelyCall(tokenList, thisLevel, true);
	}

	private static void recursivelyCall(List<Token> tokenList, Consumer<List<Token>> thisLevel,
			boolean requiresTopLevelEvaluation) {
		if (requiresTopLevelEvaluation) {
			thisLevel.accept(tokenList);
		}
		tokenList.stream().filter(token -> token instanceof ExpressionToken)
				.forEach(token -> recursivelyCall(((ExpressionToken) token).getTokens(), thisLevel,
						token.requiresTopLevelEvaluation()));
	}

	private static void resolveSigns(@NonNull List<Token> tokenList) {
		recursivelyCall(tokenList, TokenParser::resolveSign);
	}

	public static void resolvePointOperations(List<Token> tokenList) {
		recursivelyCall(tokenList, TokenParser::resolvePointOperation);
	}

	public static void resolveDashOperations(List<Token> tokenList) {
		recursivelyCall(tokenList, TokenParser::resolveDashOperation);
	}

	private static void resolveSign(List<Token> consumedTokenList) {
		if (consumedTokenList.size() == 2 && consumedTokenList.get(0).isSign()) {
			SignToken signToken = new SignToken(consumedTokenList.get(0), consumedTokenList.get(1));
			removeElementsInRange(consumedTokenList, 0, 1);
			consumedTokenList.add(0, signToken);
		}
	}

	private static void resolvePointOperation(List<Token> tokenList) {
		resolveBinaryOperation(tokenList, TOKEN_MULTIPLICATION_OPERATOR, TOKEN_DIVISION_OPERATOR);
	}

	private static void resolveDashOperation(List<Token> tokenList) {
		resolveBinaryOperation(tokenList, TOKEN_ADDITION_OPERATOR, TOKEN_SUBTRACTION_OPERATOR);
	}

	private static void resolveBinaryOperation(List<Token> tokenList, final Token operator1, final Token operator2) {
		while (tokenList.contains(operator1) || tokenList.contains(operator2)) {
			int operatorIndex = getOperatorIndex(tokenList, operator1, operator2);
			ExpressionToken expressionToken = new BinaryOperationToken(tokenList.get(operatorIndex - 1),
					tokenList.get(operatorIndex), tokenList.get(operatorIndex + 1));
			removeElementsInRange(tokenList, operatorIndex - 1, operatorIndex + 1);
			tokenList.add(operatorIndex - 1, expressionToken);
		}
	}

	private static int getOperatorIndex(List<Token> tokenList, Token operator1, Token operator2) {
		int firstOperatorIndex = tokenList.indexOf(operator1);
		int secondOperatorIndex = tokenList.indexOf(operator2);

		int operatorIndex = firstOperatorIndex == -1 ? secondOperatorIndex
				: secondOperatorIndex == -1 ? firstOperatorIndex : Math.min(firstOperatorIndex, secondOperatorIndex);
		return operatorIndex;
	}

	public static void resolveParantheses(List<Token> tokenList) {
		while (tokenList.contains(TOKEN_CLOSE_PARANTHESIS)) {
			int indexClose = tokenList.indexOf(TOKEN_CLOSE_PARANTHESIS);
			int indexOpen = getOpenParanthesis(tokenList, indexClose);
			ExpressionToken expressionToken = new ExpressionToken(
					new ArrayList<>(tokenList.subList(indexOpen + 1, indexClose)));
			removeElementsInRange(tokenList, indexOpen, indexClose);
			tokenList.add(indexOpen, expressionToken);
		}
	}

	private static void removeElementsInRange(List<?> list, int indexOpen, int indexClose) {
		for (int i = indexClose; i >= indexOpen; i--) {
			list.remove(i);
		}
	}

	private static int getOpenParanthesis(List<Token> tokenList, int indexClose) {
		for (int i = indexClose - 1; i >= 0; i--) {
			if (tokenList.get(i).isOpenParanthesis()) {
				return i;
			}
		}
		throw new IllegalArgumentException(
				"No open paranthesis found for close paranthesis at index " + indexClose + ".");
	}

}
