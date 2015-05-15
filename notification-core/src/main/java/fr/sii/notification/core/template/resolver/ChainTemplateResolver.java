package fr.sii.notification.core.template.resolver;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

public class ChainTemplateResolver implements TemplateResolver {

	private List<ConditionalResolver> resolvers;
	
	public ChainTemplateResolver(ConditionalResolver... resolvers) {
		this(Arrays.asList(resolvers));
	}
	
	public ChainTemplateResolver(List<ConditionalResolver> resolvers) {
		super();
		this.resolvers = resolvers;
	}

	@Override
	public Template getTemplate(String lookup) throws TemplateResolutionException {
		for(ConditionalResolver resolver : resolvers) {
			if(resolver.supports(lookup)) {
				return resolver.getTemplate(lookup);
			}
		}
		throw new TemplateResolutionException("No template resolver available to find template", lookup);
	}

	public ChainTemplateResolver addResolver(ConditionalResolver resolver) {
		resolvers.add(resolver);
		return this;
	}
}
