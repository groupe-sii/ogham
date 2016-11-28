package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;

/**
 * Decorates an {@link AbstractPrefixedLookupPathResolver} to manage a default
 * case (no lookup found).
 * 
 * @author Cyril Dejonghe
 *
 */
public class DefaultResourceResolver implements DelegateResourceResolver {
	/**
	 * The delegate resolver that will do the real resource resolution
	 */
	private AbstractPrefixedLookupPathResolver delegate;

	/**
	 * Initialize the resolver with the mandatory delegate.
	 * 
	 * @param delegate
	 *            the resolver that will do the real resource resolution
	 */
	public DefaultResourceResolver(AbstractPrefixedLookupPathResolver delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public ResourcePath getResourcePath(String path) {
		ResourcePath result = delegate.getResourcePath(path);
		if (result == null) {
			result = new ResourcePath(path, null, path);
		}
		return result;
	}

	@Override
	public boolean supports(String path) {
		return true;
	}

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		return delegate.getResource(getResourcePath(path));
	}

	@Override
	public ResourceResolver getActualResourceResolver() {
		return delegate instanceof DelegateResourceResolver ? ((DelegateResourceResolver) delegate).getActualResourceResolver() : delegate;
	}
}
