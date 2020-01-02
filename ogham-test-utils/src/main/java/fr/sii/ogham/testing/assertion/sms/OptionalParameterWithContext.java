package fr.sii.ogham.testing.assertion.sms;

import java.util.regex.Pattern;

import fr.sii.ogham.testing.assertion.context.Context;
import fr.sii.ogham.testing.helper.sms.bean.OptionalParameter;
import fr.sii.ogham.testing.helper.sms.bean.Tag;

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
		String result = template.replaceAll(Pattern.quote("${tagName}"), tag.getTagName());
		return parent.evaluate(result);
	}

	/**
	 * @return the optional parameter
	 */
	public OptionalParameter getParameter() {
		return parameter;
	}

}
