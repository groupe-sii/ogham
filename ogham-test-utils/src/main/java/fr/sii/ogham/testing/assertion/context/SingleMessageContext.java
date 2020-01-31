package fr.sii.ogham.testing.assertion.context;

import static java.util.regex.Pattern.quote;

public class SingleMessageContext implements Context {
	private final int index;
	public SingleMessageContext(int index) {
		super();
		this.index = index;
	}
	@Override
	public String evaluate(String template) {
		return template.replaceAll(quote("${messageIndex}"), Integer.toString(index));
	}
}