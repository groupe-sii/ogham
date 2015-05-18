package fr.sii.notification.core.template.resolver;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

public class RelativeTemplateResolver implements TemplateResolver {

	private String prefix;
	
	private String suffix;
	
	private TemplateResolver delegate;
	
	public RelativeTemplateResolver(TemplateResolver delegate, String prefix) {
		this(delegate, prefix, "");
	}
	
	public RelativeTemplateResolver(TemplateResolver delegate, String prefix, String suffix) {
		super();
		this.prefix = prefix;
		this.suffix = suffix;
		this.delegate = delegate;
	}

	@Override
	public Template getTemplate(String templateName) throws TemplateResolutionException {
		return delegate.getTemplate(templateName.startsWith("/") ? templateName : (prefix+templateName+suffix));
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
