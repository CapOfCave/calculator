package datamodel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class BinaryOperationToken extends ExpressionToken {

	private @NonNull Token leftOperand;
	private @NonNull Token operator;
	private @NonNull Token rightOperand;

	public BinaryOperationToken(@NonNull Token leftOperand, @NonNull Token operator, @NonNull Token rightOperand) {
		super(Collections.unmodifiableList(Arrays.asList(leftOperand, operator, rightOperand)));
		this.leftOperand = leftOperand;
		this.operator = operator;
		this.rightOperand = rightOperand;
	}

	@Override
	public BigDecimal calculateValue() {
		BigDecimal leftValue = leftOperand.calculateValue();
		BigDecimal rightValue = rightOperand.calculateValue();
		switch (operator.getStringValue()) {
		case "+":
			return leftValue.add(rightValue);
		case "-":
			return leftValue.subtract(rightValue);
		case "*":
			return leftValue.multiply(rightValue);
		case "/":
			return leftValue.divide(rightValue);
		default:
			throw new IllegalStateException(
					"Operator " + operator.getStringValue() + " is unknown. Only +, -, *, and / are allowed.");
		}
	}

	@Override
	public boolean requiresTopLevelEvaluation() {
		return false;
	}
}
