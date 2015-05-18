package fr.sii.notification.core.builder;

import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.core.template.resolver.TemplateResolver;

public interface TemplateParserBuilder extends Builder<TemplateParser> {
	public TemplateParserBuilder withPrefix(String prefix);
	
	public TemplateParserBuilder withSuffix(String suffix);
	
	public TemplateParserBuilder withLookupResolver(String lookup, TemplateResolver resolver);
}
