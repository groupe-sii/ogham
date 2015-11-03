package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public interface ResourceResolutionBuilder<MYSELF extends ResourceResolutionBuilder<MYSELF>> {
	MYSELF classpath(String... prefixes);
	
	MYSELF file(String... prefixes);
	
	MYSELF string(String... prefixes);
	
	MYSELF resolver(ResourceResolver resolver);
}
