package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;

/**
 * <p>
 * Decorator that delegates to other implementations. It acts as bridge between
 * {@link ConditionalResolver} and {@link Condition}. It indicates if the
 * resource can be handled by delegating to a {@link Condition}. The resource
 * resolution is also delegated to a {@link ResourceResolver}.
 * </p>
 * <p>
 * This class may be useful for a resource resolver that doesn't implement the
 * {@link ConditionalResolver} interface. It makes a basic resource resolver
 * becoming a a conditional resource resolver.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see Condition
 */
public class ConditionalDelegateResolver implements ConditionalResolver {
	/**
	 * The condition to evaluate when calling {@link #supports(String)}
	 */
	private Condition<String> condition;

	/**
	 * The resolver to call when trying to find the resource
	 */
	private ResourceResolver resolver;

	/**
	 * Initialize with a condition and a resolver.
	 * 
	 * @param condition
	 *            The condition that is evaluated when {@link #supports(String)}
	 *            is called. The condition receive the resource name as
	 *            argument
	 * @param resolver
	 *            The resolver to call if the condition has indicated that the
	 *            resource is supported
	 */
	public ConditionalDelegateResolver(Condition<String> condition, ResourceResolver resolver) {
		super();
		this.condition = condition;
		this.resolver = resolver;
	}

	@Override
	public boolean supports(String lookup) {
		return condition.accept(lookup);
	}

	@Override
	public Resource getResource(String lookup) throws ResourceResolutionException {
		return resolver.getResource(lookup);
	}
}
