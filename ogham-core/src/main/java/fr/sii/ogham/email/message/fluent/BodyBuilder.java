package fr.sii.ogham.email.message.fluent;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.fluent.SingleContentBuilder;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.email.message.Email;

/**
 * Fluent API to build a content based on:
 * <ul>
 * <li>Either a single string</li>
 * <li>Or a single template string</li>
 * <li>Or a single template loaded from a path</li>
 * <li>Or two templates (HTML and text) loaded from a path</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @since 3.0.0
 */
public class BodyBuilder {
	private final Email parent;
	private final SingleContentBuilder<Email> singleBuilder;
	private Content content;

	/**
	 * Initializes with the parent to go back to.
	 * 
	 * @param parent
	 *            the parent instance
	 */
	public BodyBuilder(Email parent) {
		super();
		this.parent = parent;
		this.singleBuilder = new SingleContentBuilder<>(parent);
	}

	/**
	 * Set the content directly as a simple string.
	 * 
	 * <p>
	 * The body will have only one part (only one main body and no alternative
	 * body). It can be either HTML or textual.
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
	public Email string(String content) {
		return singleBuilder.string(content);
	}

	/**
	 * Set the content using a template (directly provided as a string). The
	 * template contains variables that are evaluated against the bean object.
	 * 
	 * <p>
	 * The body will have only one part (only one main body and no alternative
	 * body). It can be either HTML or textual.
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
	public Email templateString(String template, Object bean) {
		return singleBuilder.templateString(template, bean);
	}

	/**
	 * Set the content using a template (directly provided as a string). The
	 * template contains variables that are evaluated against the evaluation
	 * context. The context contains at least the values of the variables but
	 * can also contain additional information for parsing the template.
	 * 
	 * <p>
	 * The body will have only one part (only one main body and no alternative
	 * body). It can be either HTML or textual.
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
	public Email templateString(String template, Context context) {
		return singleBuilder.templateString(template, context);
	}

	/**
	 * Set the content using template(s) loaded from a path. The template(s)
	 * contain variables that are evaluated against the bean object.
	 * 
	 * <p>
	 * This method lets you provide both main body and alternative body using a
	 * single path (without extension). For example:
	 * 
	 * You have two templates:
	 * <ul>
	 * <li>A textual template located at
	 * <code>/templates/email/register.txt</code> in the classpath</li>
	 * <li>An HTML template located at
	 * <code>/templates/email/register.html</code> in the classpath</li>
	 * </ul>
	 * 
	 * You can reference both templates with the same model:
	 * 
	 * <pre>
	 * {@code .template("/templates/email/register", new RegistrationContext(...))}
	 * </pre>
	 * 
	 * Both templates will be parsed using the same evaluation context. The HTML
	 * template will be used as the main body and the textual template will be
	 * used as the alternative body (when the email client can't read the HTML
	 * body).
	 * 
	 * <p>
	 * <strong>NOTE:</strong> the extensions varies according to template engine
	 * that is used to parse the template.
	 * 
	 * <p>
	 * This method is really convenient as if one template is missing (for
	 * example, you have only written the HTML template but not the textual
	 * template already), the found template is used as the main body (and the
	 * email won't have the alternative part). This way you can later add the
	 * missing template without changing your code.
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
	public Email template(String templatePath, Object bean) {
		this.content = new MultiTemplateContent(templatePath, bean);
		return parent;
	}

	/**
	 * Set the content using template(s) loaded from a path. The template(s)
	 * contain variables that are evaluated against the bean object.
	 * 
	 * <p>
	 * This method lets you provide both main body and alternative body using a
	 * single path (without extension). For example:
	 * 
	 * You have two templates:
	 * <ul>
	 * <li>A textual template located at
	 * <code>/templates/email/register.txt</code> in the classpath</li>
	 * <li>An HTML template located at
	 * <code>/templates/email/register.html</code> in the classpath</li>
	 * </ul>
	 * 
	 * You can reference both templates with the same model:
	 * 
	 * <pre>
	 * {@code .template("/templates/email/register", new RegistrationContext(...))}
	 * </pre>
	 * 
	 * Both templates will be parsed using the same evaluation context. The HTML
	 * template will be used as the main body and the textual template will be
	 * used as the alternative body (when the email client can't read the HTML
	 * body).
	 * 
	 * <p>
	 * <strong>NOTE:</strong> the extensions varies according to template engine
	 * that is used to parse the template.
	 * 
	 * <p>
	 * This method is really convenient as if one template is missing (for
	 * example, you have only written the HTML template but not the textual
	 * template already), the found template is used as the main body (and the
	 * email won't have the alternative part). This way you can later add the
	 * missing template without changing your code.
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
	public Email template(ResourcePath templatePath, Object bean) {
		this.content = new MultiTemplateContent(templatePath, bean);
		return parent;
	}

	/**
	 * Set the content using template(s) loaded from a path. The template(s)
	 * contain variables that are evaluated against the evaluation context. The
	 * context contains at least the values of the variables but can also
	 * contain additional information for parsing the template.
	 * 
	 * <p>
	 * This method lets you provide both main body and alternative body using a
	 * single path (without extension). For example:
	 * 
	 * You have two templates:
	 * <ul>
	 * <li>A textual template located at
	 * <code>/templates/email/register.txt</code> in the classpath</li>
	 * <li>An HTML template located at
	 * <code>/templates/email/register.html</code> in the classpath</li>
	 * </ul>
	 * 
	 * You can reference both templates with the same model:
	 * 
	 * <pre>
	 * {@code .template("/templates/email/register", new RegistrationContext(...))}
	 * </pre>
	 * 
	 * Both templates will be parsed using the same evaluation context. The HTML
	 * template will be used as the main body and the textual template will be
	 * used as the alternative body (when the email client can't read the HTML
	 * body).
	 * 
	 * <p>
	 * <strong>NOTE:</strong> the extensions varies according to template engine
	 * that is used to parse the template.
	 * 
	 * <p>
	 * This method is really convenient as if one template is missing (for
	 * example, you have only written the HTML template but not the textual
	 * template already), the found template is used as the main body (and the
	 * email won't have the alternative part). This way you can later add the
	 * missing template without changing your code.
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
	public Email template(String templatePath, Context context) {
		this.content = new MultiTemplateContent(templatePath, context);
		return parent;
	}

	/**
	 * Set the content using template(s) loaded from a path. The template(s)
	 * contain variables that are evaluated against the evaluation context. The
	 * context contains at least the values of the variables but can also
	 * contain additional information for parsing the template.
	 * 
	 * <p>
	 * This method lets you provide both main body and alternative body using a
	 * single path (without extension). For example:
	 * 
	 * You have two templates:
	 * <ul>
	 * <li>A textual template located at
	 * <code>/templates/email/register.txt</code> in the classpath</li>
	 * <li>An HTML template located at
	 * <code>/templates/email/register.html</code> in the classpath</li>
	 * </ul>
	 * 
	 * You can reference both templates with the same model:
	 * 
	 * <pre>
	 * {@code .template("/templates/email/register", new RegistrationContext(...))}
	 * </pre>
	 * 
	 * Both templates will be parsed using the same evaluation context. The HTML
	 * template will be used as the main body and the textual template will be
	 * used as the alternative body (when the email client can't read the HTML
	 * body).
	 * 
	 * <p>
	 * <strong>NOTE:</strong> the extensions varies according to template engine
	 * that is used to parse the template.
	 * 
	 * <p>
	 * This method is really convenient as if one template is missing (for
	 * example, you have only written the HTML template but not the textual
	 * template already), the found template is used as the main body (and the
	 * email won't have the alternative part). This way you can later add the
	 * missing template without changing your code.
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
	public Email template(ResourcePath templatePath, Context context) {
		this.content = new MultiTemplateContent(templatePath, context);
		return parent;
	}

	/**
	 * Build the final {@link Content}.
	 * 
	 * {@link #template(ResourcePath, Context)} method (and variants) preempts
	 * any call to {@link #string(String)},
	 * {@link #templateString(String, Context)} and
	 * {@link #templateString(String, Object)}.
	 * 
	 * @return the built content
	 */
	public Content build() {
		if (content != null) {
			return content;
		}
		return singleBuilder.build();
	}
}
