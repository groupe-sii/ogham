package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.core.resource.SimpleResource;

/**
 * Resource resolver that just wraps the resource string into a {@link Resource}
 * 
 * @author Aurélien Baudet
 *
 */
public class StringResourceResolver extends AbstractPrefixedLookupPathResolver implements ResourceResolver {

	public StringResourceResolver(String... lookups) {
		super(lookups);
	}

	@Override
	protected Resource getResource(ResourcePath resourcePath) throws ResourceResolutionException {
		return new SimpleResource(resourcePath.getResolvedPath().getBytes());
	}
}
