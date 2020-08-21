package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import datamodel.BinaryOperationToken;
import datamodel.ExpressionToken;
import datamodel.Token;
import main.Calculator;
import main.ExpressionParser;
import main.TokenParser;

public class CalculatorTest {

	@Test
	public void test_ExpressionParser_isValid() {
		assertTrue(ExpressionParser.isValid("11.4"));
		assertTrue(ExpressionParser.isValid("914738.42"));
		assertTrue(ExpressionParser.isValid("914738"));
		assertTrue(ExpressionParser.isValid("("));
		assertTrue(ExpressionParser.isValid(")"));
		assertTrue(ExpressionParser.isValid("( 914738.11 )"));
		assertTrue(ExpressionParser.isValid("(914738.11)"));
		assertTrue(ExpressionParser.isValid("914738   "));
		assertTrue(ExpressionParser.isValid("914738 1234"));
		assertTrue(ExpressionParser.isValid("914738 + 1234"));
		assertTrue(ExpressionParser.isValid("914738 + 1234.11"));
		assertTrue(ExpressionParser.isValid("914738 + - 1234.11"));
		assertTrue(ExpressionParser.isValid("1.91 + 2 * 3.1 - (0.3 / 10.0)"));
	}

	@Test
	public void test_ExpressionParser_parseExpression() {

		assertExpressionReturnsGroups(ExpressionParser.parseExpression("11.4"), "11.4");
		assertExpressionReturnsGroups(ExpressionParser.parseExpression("(914738.11)"), "(", "914738.11", ")");
		assertExpressionReturnsGroups(ExpressionParser.parseExpression("1.91 + 2 * 3.1 - (0.3 / 10.0)"), //
				"1.91", "+", "2", "*", "3.1", "-", "(", "0.3", "/", "10.0", ")");
	}
	
	@Test
	public void test_TokenParser_resolveParantheses_outside() {
		String[] testVals = new String[] { "(", "123.45", "+", "11", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);
		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);

		assertEquals("(123.45 + 11)", tokens.get(0).getStringValue());

	}

	@Test
	public void test_TokenParser_resolveParantheses_inside() {
		String[] testVals = new String[] { "1", "*", "(", "123.45", "+", "11", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);
		assertEquals(3, tokens.size());
		assertEquals("1", tokens.get(0).getStringValue());
		assertEquals("*", tokens.get(1).getStringValue());
		assertTrue(tokens.get(2) instanceof ExpressionToken);
		assertFalse(tokens.get(2) instanceof BinaryOperationToken);
		assertEquals("(123.45 + 11)", tokens.get(2).getStringValue());
	}

	@Test
	public void test_TokenParser_resolveParantheses_multiple() {
		String[] testVals = new String[] { "(", "1", "-", "5.5", ")", "*", "(", "123.45", "+", "11", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);
		assertEquals(3, tokens.size());
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		assertEquals("(1 - 5.5)", tokens.get(0).getStringValue());
		assertEquals("*", tokens.get(1).getStringValue());
		assertTrue(tokens.get(2) instanceof ExpressionToken);
		assertFalse(tokens.get(2) instanceof BinaryOperationToken);
		assertEquals("(123.45 + 11)", tokens.get(2).getStringValue());
	}

	@Test
	public void test_TokenParser_resolveParantheses_nested() {
		String[] testVals = new String[] { "(", "1", "-", "(", "123.45", "+", "11", ")", ")", "*", "5.5" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);
		assertEquals(3, tokens.size());

		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken expressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("(1 - (123.45 + 11))", expressionToken.getStringValue());
		assertEquals(3, expressionToken.getTokens().size());
		assertEquals("1", expressionToken.getTokens().get(0).getStringValue());
		assertEquals("-", expressionToken.getTokens().get(1).getStringValue());
		assertEquals("(123.45 + 11)", expressionToken.getTokens().get(2).getStringValue());

	}

	@Test
	public void test_TokenParser_resolvePointOperations_oneExpression() {
		String[] testVals = new String[] { "1", "*", "1" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolvePointOperations(tokens);
		assertEquals(1, tokens.size());

		assertTrue(tokens.get(0) instanceof BinaryOperationToken);
		assertEquals("(1 * 1)", tokens.get(0).getStringValue());

	}

	@Test
	public void test_TokenParser_resolvePointOperations_sequence() {
		String[] testVals = new String[] { "1", "*", "1", "/", "2" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolvePointOperations(tokens);
		assertEquals(1, tokens.size());

		assertTrue(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken expressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("((1 * 1) / 2)", expressionToken.getStringValue());
		assertEquals(3, expressionToken.getTokens().size());

	}

	@Test
	public void test_TokenParser_resolvePointOperations_insideParantheses() {
		String[] testVals = new String[] { "(", "1", "*", "1", "/", "2", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);

		TokenParser.resolvePointOperations(tokens);
		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken paranthesisExpressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("(((1 * 1) / 2))", paranthesisExpressionToken.getStringValue());

		assertEquals(1, paranthesisExpressionToken.getTokens().size());
		assertTrue(paranthesisExpressionToken.getTokens().get(0) instanceof BinaryOperationToken);
		BinaryOperationToken binaryOperationToken = (BinaryOperationToken) paranthesisExpressionToken.getTokens()
				.get(0);
		assertEquals("((1 * 1) / 2)", binaryOperationToken.getStringValue());
		assertEquals("2", binaryOperationToken.getRightOperand().getStringValue());
		assertEquals("/", binaryOperationToken.getOperator().getStringValue());
		BinaryOperationToken leftOperand = (BinaryOperationToken) binaryOperationToken.getLeftOperand();
		assertEquals("(1 * 1)", leftOperand.getStringValue());
		assertEquals("1", leftOperand.getLeftOperand().getStringValue());
		assertEquals("*", leftOperand.getOperator().getStringValue());
		assertEquals("1", leftOperand.getRightOperand().getStringValue());
	}

	@Test
	public void test_TokenParser_resolvePointOperations_deepInsideParantheses() {
		String[] testVals = new String[] { "(", "2", "*", "(", "1", "*", "3", "/", "2", ")", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);

		TokenParser.resolvePointOperations(tokens);

		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken t1 = (ExpressionToken) tokens.get(0);

		assertEquals(1, t1.getTokens().size());
		assertTrue(t1.getTokens().get(0) instanceof BinaryOperationToken);
		BinaryOperationToken t2 = (BinaryOperationToken) t1.getTokens().get(0);

		assertEquals("2", t2.getLeftOperand().getStringValue());
		assertEquals("*", t2.getOperator().getStringValue());

		assertTrue(t2.getRightOperand() instanceof ExpressionToken);
		assertFalse(t2.getRightOperand() instanceof BinaryOperationToken);
		ExpressionToken t3 = (ExpressionToken) t2.getRightOperand();

		assertEquals(1, t3.getTokens().size());
		assertTrue(t3.getTokens().get(0) instanceof BinaryOperationToken);
		BinaryOperationToken t4 = (BinaryOperationToken) t3.getTokens().get(0);

		assertEquals("/", t4.getOperator().getStringValue());
		assertEquals("2", t4.getRightOperand().getStringValue());

		assertTrue(t4.getLeftOperand() instanceof BinaryOperationToken);
		BinaryOperationToken t5 = (BinaryOperationToken) t4.getLeftOperand();

		assertEquals("1", t5.getLeftOperand().getStringValue());
		assertEquals("*", t5.getOperator().getStringValue());
		assertEquals("3", t5.getRightOperand().getStringValue());

	}

	@Test
	public void test_TokenParser_resolveDashOperations_oneExpression() {
		String[] testVals = new String[] { "1", "+", "1" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveDashOperations(tokens);
		assertEquals(1, tokens.size());

		assertTrue(tokens.get(0) instanceof BinaryOperationToken);
		assertEquals("(1 + 1)", tokens.get(0).getStringValue());

	}

	@Test
	public void test_TokenParser_resolveDashOperations_sequence() {
		String[] testVals = new String[] { "1", "+", "1", "-", "2" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveDashOperations(tokens);
		assertEquals(1, tokens.size());

		assertTrue(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken expressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("((1 + 1) - 2)", expressionToken.getStringValue());
		assertEquals(3, expressionToken.getTokens().size());

	}

	@Test
	public void test_TokenParser_resolveDashOperations_insideParantheses() {
		String[] testVals = new String[] { "(", "1", "+", "1", "-", "2", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);

		TokenParser.resolveDashOperations(tokens);
		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken paranthesisExpressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("(((1 + 1) - 2))", paranthesisExpressionToken.getStringValue());

		assertEquals(1, paranthesisExpressionToken.getTokens().size());
		assertTrue(paranthesisExpressionToken.getTokens().get(0) instanceof BinaryOperationToken);
		BinaryOperationToken binaryOperationToken = (BinaryOperationToken) paranthesisExpressionToken.getTokens()
				.get(0);
		assertEquals("((1 + 1) - 2)", binaryOperationToken.getStringValue());
		assertEquals("2", binaryOperationToken.getRightOperand().getStringValue());
		assertEquals("-", binaryOperationToken.getOperator().getStringValue());
		BinaryOperationToken leftOperand = (BinaryOperationToken) binaryOperationToken.getLeftOperand();
		assertEquals("(1 + 1)", leftOperand.getStringValue());
		assertEquals("1", leftOperand.getLeftOperand().getStringValue());
		assertEquals("+", leftOperand.getOperator().getStringValue());
		assertEquals("1", leftOperand.getRightOperand().getStringValue());
	}

	@Test
	public void test_TokenParser_resolveDashOperations_deepInsideParantheses() {
		String[] testVals = new String[] { "(", "2", "+", "(", "1", "+", "3", "-", "2", ")", ")" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);

		TokenParser.resolveDashOperations(tokens);

		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof ExpressionToken);
		assertFalse(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken t1 = (ExpressionToken) tokens.get(0);

		assertEquals(1, t1.getTokens().size());
		assertTrue(t1.getTokens().get(0) instanceof BinaryOperationToken);
		BinaryOperationToken t2 = (BinaryOperationToken) t1.getTokens().get(0);

		assertEquals("2", t2.getLeftOperand().getStringValue());
		assertEquals("+", t2.getOperator().getStringValue());

		assertTrue(t2.getRightOperand() instanceof ExpressionToken);
		assertFalse(t2.getRightOperand() instanceof BinaryOperationToken);
		ExpressionToken t3 = (ExpressionToken) t2.getRightOperand();

		assertEquals(1, t3.getTokens().size());
		assertTrue(t3.getTokens().get(0) instanceof BinaryOperationToken);
		BinaryOperationToken t4 = (BinaryOperationToken) t3.getTokens().get(0);

		assertEquals("-", t4.getOperator().getStringValue());
		assertEquals("2", t4.getRightOperand().getStringValue());

		assertTrue(t4.getLeftOperand() instanceof BinaryOperationToken);
		BinaryOperationToken t5 = (BinaryOperationToken) t4.getLeftOperand();

		assertEquals("1", t5.getLeftOperand().getStringValue());
		assertEquals("+", t5.getOperator().getStringValue());
		assertEquals("3", t5.getRightOperand().getStringValue());

	}

	@Test
	public void test_TokenParser_resolveDashOperations_withPointOperations() {
		String[] testVals = new String[] { "1", "+", "1", "*", "2" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolvePointOperations(tokens);

		TokenParser.resolveDashOperations(tokens);

		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken expressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("(1 + (1 * 2))", expressionToken.getStringValue());
		assertEquals(3, expressionToken.getTokens().size());

	}

	@Test
	public void test_TokenParser_resolveDashOperations_withPointOperationsAndParantheses() {
		String[] testVals = new String[] { "(", "1", "+", "1", ")", "*", "2" };
		List<Token> tokens = toList(testVals);
		TokenParser.resolveParantheses(tokens);
		TokenParser.resolvePointOperations(tokens);

		TokenParser.resolveDashOperations(tokens);

		assertEquals(1, tokens.size());
		assertTrue(tokens.get(0) instanceof BinaryOperationToken);
		ExpressionToken expressionToken = (ExpressionToken) tokens.get(0);
		assertEquals("(((1 + 1)) * 2)", expressionToken.getStringValue());
		assertEquals(3, expressionToken.getTokens().size());

	}

	@Test
	public void test_Calculator_calculate_multplication() throws InterruptedException {
		assertEquals(5, Calculator.calculate("2.5 * 2").intValue());
	}

	@Test
	public void test_Calculator_calculate_division() throws InterruptedException {
		assertEquals(BigDecimal.valueOf(2), Calculator.calculate("3 / 1.5"));
	}

	@Test
	public void test_Calculator_calculate_addition() throws InterruptedException {
		assertEquals(BigDecimal.valueOf(5.5), Calculator.calculate("2.2 + 3.3"));
	}

	@Test
	public void test_Calculator_calculate_subtraction() throws InterruptedException {
		assertEquals(BigDecimal.valueOf(-0.5), Calculator.calculate("2 - 2.5"));
	}

	@Test
	public void test_Calculator_calculate_chained() throws InterruptedException {
		assertEquals(BigDecimal.valueOf(-2), Calculator.calculate("2 + 3 - 7"));
	}

	@Test
	public void test_Calculator_calculate_dashAndPoint() {
		assertEquals(-5, Calculator.calculate("2 - 2 * 3.5").intValue());
	}

	@Test
	public void test_Calculator_calculate_parantheses() {
		assertEquals(0, Calculator.calculate("(2 - 2) * 3.5").intValue());
	}

	@Test
	public void test_Calculator_calculate_multipleParantheses() {
		assertEquals(6, Calculator.calculate("(2 + 2) * (3 / 2)").intValue());
	}

	@Test
	public void test_Calculator_calculate_nestedParantheses() {
		assertEquals(10, Calculator.calculate("2 * (2 + 2 * (3 / 2))").intValue());
	}

	@Test
	public void test_Calculator_calculate_sign() {
		assertEquals(-7, Calculator.calculate("(- 2) * 3.5").intValue());
	}

	@Test
	public void test_Calculator_calculate_sign_binaryOperation() {
		assertEquals(7, Calculator.calculate("(-(2 * (- 1))) * 3.5").intValue());
	}

	@Test
	public void test_Calculator_calculate_multiple_signs() {
		assertEquals(3, Calculator.calculate("(-(-1)) * 3").intValue());
	}

	private List<Token> toList(String[] testVals) {
		List<Token> tokens = Arrays.stream(testVals).map(value -> Token.of(value)).collect(Collectors.toList());
		return tokens;
	}

	private void assertExpressionReturnsGroups(List<Token> parsedExpression, String... groups) {
		assertEquals(groups.length, parsedExpression.size());
		for (int i = 0; i < parsedExpression.size(); i++) {
			assertEquals(Token.of(groups[i]), parsedExpression.get(i));
		}
	}
}
