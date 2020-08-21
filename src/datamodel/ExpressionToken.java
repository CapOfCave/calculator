package datamodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ExpressionToken extends Token {

	@NonNull
	private List<Token> tokens;

	public ExpressionToken(List<Token> tokens) {
		if (tokens.isEmpty()) {
			new IllegalStateException("ExpressionToken must consist of at least one token");
		}
		this.tokens = tokens;
	}

	@Override
	public @NonNull String getStringValue() {
		return "(" + String.join(" ", tokens.stream().map(token -> token.getStringValue()).collect(Collectors.toList()))
				+ ")";
	}

	@Override
	public BigDecimal calculateValue() {
		if (tokens.size() == 1) {
			return tokens.get(0).calculateValue();
		}
		throw new IllegalStateException("Token list for expression " + getStringValue()
				+ "must have size 1, but has size " + tokens.size() + ".");

	}
	
	@Override
	public boolean requiresTopLevelEvaluation() {
		return true;
	}

}