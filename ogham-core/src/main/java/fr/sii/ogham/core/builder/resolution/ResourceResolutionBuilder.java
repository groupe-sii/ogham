package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public interface ResourceResolutionBuilder<MYSELF extends ResourceResolutionBuilder<MYSELF>> {
	ClassPathResolutionBuilder<MYSELF> classpath();
	
	FileResolutionBuilder<MYSELF> file();

	StringResolutionBuilder<MYSELF> string();
	
	MYSELF resolver(ResourceResolver resolver);
}
