package fr.sii.ogham.assertion.context;

import java.util.regex.Pattern;

public class SingleMessageContext implements Context {
	private final int index;
	public SingleMessageContext(int index) {
		super();
		this.index = index;
	}
	@Override
	public String evaluate(String template) {
		return template.replaceAll(Pattern.quote("${messageIndex}"), Integer.toString(index));
	}
}