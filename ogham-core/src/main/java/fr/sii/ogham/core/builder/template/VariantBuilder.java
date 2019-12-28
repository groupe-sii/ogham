package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.message.content.EmailVariant;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.template.common.adapter.VariantResolver;

/**
 * Configures {@link Variant} handling. You can register a custom variant with a
 * custom extension.
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 */
@SuppressWarnings("squid:S00119")
public interface VariantBuilder<MYSELF> {
	/**
	 * Registers a custom variant with its extension. The aim is to be able to
	 * define a custom extension that is independent of the template parser and
	 * automatically load templates without specifying the extension allowing to
	 * mix several template parsers.
	 * 
	 * For example, Freemarker is configured that way:
	 * 
	 * <pre>
	 * .variant(EmailVariant.HTML, "html.ftl")
	 * .variant(EmailVariant.TEXT, "txt.ftl")
	 * </pre>
	 * 
	 * And Thymeleaf is configured that way:
	 * 
	 * <pre>
	 * .variant(EmailVariant.HTML, "html")
	 * .variant(EmailVariant.TEXT, "txt")
	 * </pre>
	 * 
	 * Then when you send an email using templates:
	 * 
	 * <pre>
	 * messagingService.send(new Email()
	 * 		// use default predefined variants (EmailVariant.TEXT and
	 * 		// EmailVariant.HTML)
	 * 		.content(new MultiTemplateContent("simple", new SimpleBean("foo", 42))));
	 * </pre>
	 * 
	 * You can either use different template files:
	 * <ul>
	 * <li>"simple.html" and "simple.txt" that will be both handled by
	 * Thymeleaf</li>
	 * <li>"simple.html.ftl" and "simple.txt.ftl" that will be both handled by
	 * Freemarker</li>
	 * <li>"simple.html" and "simple.txt.ftl", the first will be handled by
	 * Thymeleaf and the second by Freemarker</li>
	 * <li>"simple.html.ftl" and "simple.txt", the first will be handled by
	 * Freemarker and the second by Thymeleaf</li>
	 * </ul>
	 * 
	 * <p>
	 * Registering your custom variant like this:
	 * 
	 * <pre>
	 * // for Freemarker 
	 * .variant(MyVariant, "foo")
	 * // for Thymeleaf
	 * .variant(MyVariant, "bar")
	 * </pre>
	 * 
	 * You can now use your own variant (your own extensions) for loading
	 * template files independently of the template parser.
	 * 
	 * <pre>
	 * messagingService.send(new Email()
	 * 		// use your custom variant
	 * 		.content(new MultiTemplateContent("simple", new SimpleBean("foo", 42), EmailVariant.TEXT, MyVariant, EmailVariant.HTML)));
	 * </pre>
	 * 
	 * The system will load any template file with ".foo" or ".bar" extensions
	 * if they exist.
	 * 
	 * @param variant
	 *            the variant (may be a enum like {@link EmailVariant})
	 * @param extension
	 *            the extension that corresponds to the variant
	 * @return this instance for fluent chaining
	 */
	MYSELF variant(Variant variant, String extension);

	/**
	 * Instantiates and configures the variant resolution based on previously
	 * defined variants.
	 * 
	 * @return the variant resolver
	 */
	VariantResolver buildVariant();
}
