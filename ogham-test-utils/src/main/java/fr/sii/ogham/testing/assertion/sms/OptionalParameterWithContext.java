package fr.sii.ogham.testing.assertion.sms;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;

import fr.sii.ogham.testing.assertion.context.Context;
import fr.sii.ogham.testing.sms.simulator.bean.OptionalParameter;
import fr.sii.ogham.testing.sms.simulator.bean.Tag;

/**
 * Dedicated context for one particular optional parameter (identified by the
 * tag).
 * 
 * @author Aurélien Baudet
 *
 */
public class OptionalParameterWithContext implements Context {
	private final Tag tag;
	private final OptionalParameter parameter;
	private final Context parent;

	/**
	 * 
	 * @param tag
	 *            the tag of the optional parameter
	 * @param parameter
	 *            the optional parameter (Tag-Length-Value)
	 * @param parent
	 *            the parent context
	 */
	public OptionalParameterWithContext(Tag tag, OptionalParameter parameter, Context parent) {
		super();
		this.tag = tag;
		this.parameter = parameter;
		this.parent = parent;
	}

	@Override
	public String evaluate(String template) {
		String result = template.replaceAll(quote("${tagName}"), quoteReplacement(tag.getTagName()));
		result = result.replaceAll(quote("${found}"), quoteReplacement(parameterFound() ? "" : " (/!\\ not found)"));
		return parent.evaluate(result);
	}

	private boolean parameterFound() {
		return parameter != null && parameter.getTag() != null;
	}

	/**
	 * @return the optional parameter
	 */
	public OptionalParameter getParameter() {
		return parameter;
	}

}
