package fr.sii.ogham.core.resource.resolver;

public interface DelegateResourceResolver extends ResourceResolver {
	/**
	 * Finds the final delegated {@link ResourceResolver} by any level of
	 * delegation.
	 * 
	 * @return a {@link ResourceResolver} that can be translated to an template
	 *         engine adapter
	 */
	ResourceResolver getActualResourceResolver();
}
