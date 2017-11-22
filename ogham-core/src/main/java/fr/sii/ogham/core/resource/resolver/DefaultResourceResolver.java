package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.ResourcePath;

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
	public ResolvedPath resolve(ResourcePath path) {
		ResolvedPath result = delegate.resolve(path);
		if (result == null) {
			result = new ResolvedResourcePath(path, null, path.getOriginalPath());
		}
		return result;
	}

	@Override
	public boolean supports(ResourcePath path) {
		return true;
	}

	@Override
	public Resource getResource(ResourcePath path) throws ResourceResolutionException {
		return delegate.getResource(resolve(path));
	}

	@Override
	public ResourceResolver getActualResourceResolver() {
		return delegate instanceof DelegateResourceResolver ? ((DelegateResourceResolver) delegate).getActualResourceResolver() : delegate;
	}
}
