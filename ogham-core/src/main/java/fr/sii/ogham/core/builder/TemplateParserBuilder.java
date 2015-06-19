package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.parser.TemplateParser;

/**
 * Define a builder for a template engine implementation. It provides general
 * methods for all template engines. It helps to construct the object using a
 * fluent interface.
 * 
 * @author Aurélien Baudet
 *
 */
public interface TemplateParserBuilder extends Builder<TemplateParser> {
	/**
	 * Set the prefix for template lookup. This prefix is used for all lookup
	 * methods. The aim is to define only the name of the template (or a subset)
	 * and the system will find it for you. It avoids to explicitly write the
	 * whole path and let you change the lookup method easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>/foo/template/createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>/foo/template/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the prefix to <code>/foo/template/</code> and then
	 * reference the templates using the file name:
	 * <ul>
	 * <li><code>createAccount.html</code></li>
	 * <li><code>resetPassword.html</code></li>
	 * </ul>
	 * 
	 * @param prefix
	 *            the prefix for template resolution
	 * @return The current builder for fluent use
	 */
	public TemplateParserBuilder withPrefix(String prefix);

	/**
	 * Set the suffix for template lookup. This suffix is used for all lookup
	 * methods. The aim is to define only the name of the template (or a subset)
	 * and the system will find it for you. It avoids to explicitly write the
	 * whole path and let you change the lookup method easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>/foo/template/createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>/foo/template/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the prefix to <code>/foo/template/</code>, the suffix to
	 * <code>.html</code> and then reference the templates using the file name:
	 * <ul>
	 * <li><code>createAccount</code></li>
	 * <li><code>resetPassword</code></li>
	 * </ul>
	 * 
	 * @param suffix
	 *            the suffix for template resolution
	 * @return The current builder for fluent use
	 */
	public TemplateParserBuilder withSuffix(String suffix);

	/**
	 * Register a lookup resolver. The lookup is like JNDI lookup. It indicates
	 * using a simple string how to handle the provided path or URL.
	 * 
	 * For example:
	 * <ul>
	 * <li><code>"classpath:/foo"</code> indicates that the provided path
	 * represents a classpath entry.</li>
	 * <li><code>"file:/tmp"</code> indicates that the provided path represents
	 * a file located on the system.</li>
	 * </ul>
	 * 
	 * @param lookup
	 *            the lookup name (without the : character)
	 * @param resolver
	 *            the resolver implementation
	 * @return The current builder for fluent use
	 */
	public TemplateParserBuilder withLookupResolver(String lookup, ResourceResolver resolver);
}
