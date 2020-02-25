package fr.sii.ogham.core.message.fluent;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.StringTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;

/**
 * Fluent API to build a single content based on:
 * <ul>
 * <li>Either a string</li>
 * <li>Or a template string</li>
 * <li>Or a template loaded from a path</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent for fluent chaining
 * @since 3.0.0
 */
public class SingleContentBuilder<P> {
	private final P parent;
	private Content content;

	/**
	 * Initializes with the parent to go back to.
	 * 
	 * @param parent
	 *            the parent instance
	 */
	public SingleContentBuilder(P parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Set the content directly as a simple string.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param content
	 *            the content as a string
	 * @return the instance for fluent chaining
	 */
	public P string(String content) {
		this.content = new StringContent(content);
		return parent;
	}

	/**
	 * Set the content using a template (directly provided as a string). The
	 * template contains variables that are evaluated against the bean object.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param template
	 *            the template directly provided as a string
	 * @param bean
	 *            the object that contains the variables that are referenced in
	 *            the template
	 * @return the instance for fluent chaining
	 */
	public P templateString(String template, Object bean) {
		this.content = new StringTemplateContent(template, bean);
		return parent;
	}

	/**
	 * Set the content using a template (directly provided as a string). The
	 * template contains variables that are evaluated against the evaluation
	 * context. The context contains at least the values of the variables but
	 * can also contain additional information for parsing the template.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param template
	 *            the template directly provided as a string
	 * @param context
	 *            contains at least the variables that are referenced in the
	 *            template and may contain additional information to parse the
	 *            template
	 * @return the instance for fluent chaining
	 */
	public P templateString(String template, Context context) {
		this.content = new StringTemplateContent(template, context);
		return parent;
	}

	/**
	 * Set the content using a template loaded from a path. The template
	 * contains variables that are evaluated against the bean object.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param templatePath
	 *            the path to the template
	 * @param bean
	 *            the object that contains the variables that are referenced in
	 *            the template
	 * @return the instance for fluent chaining
	 */
	public P template(String templatePath, Object bean) {
		this.content = new TemplateContent(templatePath, bean);
		return parent;
	}

	/**
	 * Set the content using a template loaded from a path. The template
	 * contains variables that are evaluated against the bean object.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param templatePath
	 *            the path to the template
	 * @param bean
	 *            the object that contains the variables that are referenced in
	 *            the template
	 * @return the instance for fluent chaining
	 */
	public P template(ResourcePath templatePath, Object bean) {
		this.content = new TemplateContent(templatePath, bean);
		return parent;
	}

	/**
	 * Set the content using a template loaded from a path. The template
	 * contains variables that are evaluated against the evaluation context. The
	 * context contains at least the values of the variables but can also
	 * contain additional information for parsing the template.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param templatePath
	 *            the path to the template
	 * @param context
	 *            contains at least the variables that are referenced in the
	 *            template and may contain additional information to parse the
	 *            template
	 * @return the instance for fluent chaining
	 */
	public P template(String templatePath, Context context) {
		this.content = new TemplateContent(templatePath, context);
		return parent;
	}

	/**
	 * Set the content using a template loaded from a path. The template
	 * contains variables that are evaluated against the evaluation context. The
	 * context contains at least the values of the variables but can also
	 * contain additional information for parsing the template.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If any other method of this class was called before calling this method,
	 * only the value of this method is used.
	 * 
	 * @param templatePath
	 *            the path to the template
	 * @param context
	 *            contains at least the variables that are referenced in the
	 *            template and may contain additional information to parse the
	 *            template
	 * @return the instance for fluent chaining
	 */
	public P template(ResourcePath templatePath, Context context) {
		this.content = new TemplateContent(templatePath, context);
		return parent;
	}

	/**
	 * Build the final {@link Content}. Only the last registered content is used
	 * (the last call to any method of this class)
	 * 
	 * @return the built content
	 */
	public Content build() {
		return content;
	}
}
