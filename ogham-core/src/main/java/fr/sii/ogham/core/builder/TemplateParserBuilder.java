package fr.sii.ogham.core.builder;

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
	 * Set the parent path for template lookup. This parent path is used for all
	 * lookup methods. The aim is to define only the name of the template (or a
	 * subset) and the system will find it for you. It avoids to explicitly
	 * write the whole path and let you change the lookup method easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>/foo/template/createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>/foo/template/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the parent path to <code>/foo/template/</code> and then
	 * reference the templates using the file name:
	 * <ul>
	 * <li><code>createAccount.html</code></li>
	 * <li><code>resetPassword.html</code></li>
	 * </ul>
	 * 
	 * @param parentPath
	 *            the parent path for template resolution
	 * @return The current builder for fluent use
	 */
	public TemplateParserBuilder withParentPath(String parentPath);

	/**
	 * Set the extension for template lookup. This extension is used for all
	 * lookup methods. The aim is to define only the name of the template (or a
	 * subset) and the system will find it for you. It avoids to explicitly
	 * write the whole path and let you change the lookup method easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>/foo/template/createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>/foo/template/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the parent path to <code>/foo/template/</code>, the
	 * extension to <code>.html</code> and then reference the templates using
	 * the file name:
	 * <ul>
	 * <li><code>createAccount</code></li>
	 * <li><code>resetPassword</code></li>
	 * </ul>
	 * 
	 * @param extension
	 *            the extension for template resolution
	 * @return The current builder for fluent use
	 */
	public TemplateParserBuilder withExtension(String extension);

}
