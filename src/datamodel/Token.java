package datamodel;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Token {
	@EqualsAndHashCode.Include
	private final String stringValue;

	public Token() {
		this(null);
	}

	public @NonNull String getStringValue() {
		return stringValue;
	}

	public boolean isOpenParanthesis() {
		return getStringValue().equals("(");
	}

	public BigDecimal calculateValue() {
		// Assume type is numeric
		return new BigDecimal(stringValue);
	}

	public boolean requiresTopLevelEvaluation() {
		return false;
	}

	public boolean isSign() {
		return this.getStringValue().equals("+") || this.getStringValue().equals("-");
	}

}
