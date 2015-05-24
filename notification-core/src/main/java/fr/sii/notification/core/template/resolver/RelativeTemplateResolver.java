package fr.sii.notification.core.template.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

/**
 * <p>
 * Decorator template resolver that use prefix and suffix for template
 * resolution.
 * </p>
 * <p>
 * For example, the prefix values "email/user/" and the suffix is ".html". The
 * template name is "hello". This template resolver appends the prefix, the
 * template name and the suffix generating the path "email/user/hello.html".
 * </p>
 * <p>
 * Once the path is generated, then this implementation delegates the real
 * template resolution to another implementation.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class RelativeTemplateResolver implements TemplateResolver {
	private static final Logger LOG = LoggerFactory.getLogger(RelativeTemplateResolver.class);

	/**
	 * The prefix to add to the template name (or path)
	 */
	private String prefix;

	/**
	 * The suffix to add to the template name (or path)
	 */
	private String suffix;

	/**
	 * The delegate resolver that will do the real template resolution
	 */
	private TemplateResolver delegate;

	/**
	 * Initialize the resolver with the mandatory delegate and a prefix. No
	 * suffix will be appended to the template path.
	 * 
	 * @param delegate
	 *            the resolver that will do the real template resolution
	 * @param prefix
	 *            a string to add before the template path
	 */
	public RelativeTemplateResolver(TemplateResolver delegate, String prefix) {
		this(delegate, prefix, "");
	}

	/**
	 * Initialize the resolver with the mandatory delegate, a prefix and a
	 * suffix.
	 * 
	 * @param delegate
	 *            the resolver that will do the real template resolution
	 * @param prefix
	 *            a string to add before the template path
	 * @param suffix
	 *            a string to add after the template path
	 */
	public RelativeTemplateResolver(TemplateResolver delegate, String prefix, String suffix) {
		super();
		this.prefix = prefix == null ? "" : prefix;
		this.suffix = suffix == null ? "" : suffix;
		this.delegate = delegate;
	}

	@Override
	public Template getTemplate(String templateName) throws TemplateResolutionException {
		boolean absolute = templateName.startsWith("/");
		if(absolute) {
			LOG.trace("Absolute template path {} => do not add prefix/suffix", templateName);
			return delegate.getTemplate(templateName);
		} else {
			LOG.debug("Adding prefix ({}) and suffix ({}) to the template path {}", prefix, suffix, templateName);
			return delegate.getTemplate(prefix + templateName + suffix);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public TemplateResolver getDelegate() {
		return delegate;
	}
}
