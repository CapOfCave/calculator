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
public class SignToken extends ExpressionToken {
	private @NonNull Token sign;
	private @NonNull Token value;

	public SignToken(@NonNull Token sign, @NonNull Token value) {
		super(Collections.unmodifiableList(Arrays.asList(sign, value)));
		this.sign = sign;
		this.value = value;
	}
	
	@Override
	public BigDecimal calculateValue() {
		if (sign.getStringValue().equals("-")) {
			return value.calculateValue().negate();
		}
		return value.calculateValue();
	}
	
	@Override
	public boolean requiresTopLevelEvaluation() {
		return false;
	}
}
