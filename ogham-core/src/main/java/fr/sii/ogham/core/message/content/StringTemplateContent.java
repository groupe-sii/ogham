package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.template.context.Context;

/**
 * <p>
 * Content that contains a template. The template contains variables. The
 * template will be evaluated with the provided context (variable values).
 * </p>
 * This is a shortcut to:
 * <pre>
 * new TemplateContent(&quot;string:&lt;template content&gt;&quot;, context);
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class StringTemplateContent extends TemplateContent {

	/**
	 * Initialize the content with the template and the context.
	 * 
	 * @param template
	 *            the template
	 * @param context
	 *            the context (variable values)
	 */
	public StringTemplateContent(String template, Context context) {
		super("string:" + template, context);
	}

	/**
	 * Shortcut for directly using any object as source for variable
	 * substitutions.
	 * 
	 * @param template
	 *            the template
	 * @param bean
	 *            the object that contains the variable values
	 */
	public StringTemplateContent(String template, Object bean) {
		super("string:" + template, bean);
	}
}
