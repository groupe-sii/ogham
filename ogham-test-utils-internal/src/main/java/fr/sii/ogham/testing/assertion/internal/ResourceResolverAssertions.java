package fr.sii.ogham.testing.assertion.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;

import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on resource resolution.
 * 
 * 
 * <pre>
 * {@code
 *   classpath()
 *     .pathPrefix(is("prefix/")
 * }
 * </pre>
 * 
 * @param <P>
 *            the parent type
 */
public class ResourceResolverAssertions<P> extends HasParent<P> {
	private final Set<ResourceResolver> resolvers;

	public ResourceResolverAssertions(P parent, Set<ResourceResolver> resourceResolvers) {
		super(parent);
		this.resolvers = resourceResolvers;
	}

	/**
	 * Make assertions on classpath resolution.
	 * 
	 * <pre>
	 * {@code
	 * .pathPrefix(is("/custom/"))
	 * .pathSuffix(is(".html"))
	 * }
	 * </pre>
	 * 
	 * @return the builder for fluent chaining
	 */
	public RelativeResolutionAssertions<ResourceResolverAssertions<P>> classpath() {
		return new RelativeResolutionAssertions<>(this, "classpath", getRelativeResolversFor(ClassPathResolver.class));
	}

	/**
	 * Make assertions on file resolution.
	 * 
	 * <pre>
	 * {@code
	 * .pathPrefix(is("/custom/"))
	 * .pathSuffix(is(".html"))
	 * }
	 * </pre>
	 * 
	 * @return the builder for fluent chaining
	 */
	public RelativeResolutionAssertions<ResourceResolverAssertions<P>> file() {
		return new RelativeResolutionAssertions<>(this, "file", getRelativeResolversFor(FileResolver.class));
	}

	private <T> Set<RelativeResolver> getRelativeResolversFor(Class<T> resolverClass) {
		Set<RelativeResolver> found = new HashSet<>();
		for (ResourceResolver resolver : resolvers) {
			Set<RelativeResolver> relativeResolvers = findResolvers(resolver, RelativeResolver.class);
			for (RelativeResolver relative : relativeResolvers) {
				if (resolverClass.isAssignableFrom(relative.getActualResourceResolver().getClass())) {
					found.add(relative);
				}
			}
		}
		return found;
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> findResolvers(ResourceResolver resolver, Class<T> resolverClass) {
		Set<T> found = new HashSet<>();
		if (resolverClass.isAssignableFrom(resolver.getClass())) {
			found.add((T) resolver);
		}
		if (resolver instanceof FirstSupportingResourceResolver) {
			found.addAll(findResolvers((FirstSupportingResourceResolver) resolver, resolverClass));
		}
		if (resolver instanceof RelativeResolver) {
			found.addAll(findResolvers((RelativeResolver) resolver, resolverClass));
		}
		return found;
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> findResolvers(FirstSupportingResourceResolver resolver, Class<T> resolverClass) {
		try {
			Set<T> found = new HashSet<>();
			List<ResourceResolver> resolvers = (List<ResourceResolver>) FieldUtils.readField(resolver, "resolvers", true);
			for (ResourceResolver r : resolvers) {
				found.addAll(findResolvers(r, resolverClass));
			}
			return found;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'resolvers' field of FirstSupportingResourceResolver", e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> findResolvers(RelativeResolver resolver, Class<T> resolverClass) {
		try {
			Set<T> found = new HashSet<>();
			if (resolverClass.isAssignableFrom(resolver.getClass())) {
				found.add((T) resolver);
			}
			ResourceResolver delegate = (ResourceResolver) FieldUtils.readField(resolver, "delegate", true);
			if (resolverClass.isAssignableFrom(resolver.getActualResourceResolver().getClass())) {
				found.add((T) delegate);
			}
			return found;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'delegate' field of RelativeResolver", e);
		}
	}

}
