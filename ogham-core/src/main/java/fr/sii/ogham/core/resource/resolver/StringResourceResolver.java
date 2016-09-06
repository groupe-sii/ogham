package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.SimpleResource;

/**
 * Resource resolver that just wraps the resource string into a {@link Resource}
 * 
 * @author Aurélien Baudet
 *
 */
public class StringResourceResolver extends AbstractPrefixedLookupPathResolver implements ResourceResolver {

	public StringResourceResolver(boolean isDefault, String... lookups) {
		super(isDefault, lookups);
	}

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		return new SimpleResource(path.getBytes());
	}
}
