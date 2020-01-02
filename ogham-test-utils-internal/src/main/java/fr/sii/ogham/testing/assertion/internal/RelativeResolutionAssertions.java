package fr.sii.ogham.testing.assertion.internal;

import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Matcher;

import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.testing.assertion.HasParent;

/**
 * Make assertions on resource resolution.
 * 
 * <pre>
 * {@code
 * .pathPrefix(is("/custom/"))
 * .pathSuffix(is(".html"))
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the parent type
 */
public class RelativeResolutionAssertions<P> extends HasParent<P> {
	private final String name;
	private final Set<RelativeResolver> relativeResolvers;

	public RelativeResolutionAssertions(P parent, String name, Set<RelativeResolver> relativeResolvers) {
		super(parent);
		this.name = name;
		this.relativeResolvers = relativeResolvers;
	}

	/**
	 * Ensures that path prefix is correctly configured.
	 * 
	 * @param matcher
	 *            the matcher to ensure that path prefix is correct
	 * @return this instance for fluent chaining
	 */
	public RelativeResolutionAssertions<P> pathPrefix(Matcher<? super String> matcher) {
		for (RelativeResolver resolver : relativeResolvers) {
			assertThat(name + " prefix", getPrefix(resolver), matcher);
		}
		return this;
	}

	/**
	 * Ensures that path suffix is correctly configured.
	 * 
	 * @param matcher
	 *            the matcher to ensure that path suffix is correct
	 * @return this instance for fluent chaining
	 */
	public RelativeResolutionAssertions<P> pathSuffix(Matcher<? super String> matcher) {
		for (RelativeResolver resolver : relativeResolvers) {
			assertThat(name + " suffix", getSuffix(resolver), matcher);
		}
		return this;
	}

	/**
	 * Ensures that lookup prefixes ("classpath:", "file:", ...) are correctly
	 * configured.
	 * 
	 * @param matcher
	 *            the matcher to ensure that lookup prefixes are correct
	 * @return this instance for fluent chaining
	 */
	public RelativeResolutionAssertions<P> lookup(Matcher<? super List<String>> matcher) {
		for (RelativeResolver resolver : relativeResolvers) {
			assertThat(name + " lookups", getLookups(resolver), matcher);
		}
		return this;
	}

	private static String getPrefix(RelativeResolver resolver) {
		try {
			return (String) FieldUtils.readField(resolver, "parentPath", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'parentPath' field of RelativeResolver", e);
		}
	}

	private static String getSuffix(RelativeResolver resolver) {
		try {
			return (String) FieldUtils.readField(resolver, "extension", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'extension' field of RelativeResolver", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static List<String> getLookups(RelativeResolver resolver) {
		try {
			return (List<String>) FieldUtils.readField(resolver.getActualResourceResolver(), "lookups", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'lookups' field of " + resolver.getClass().getSimpleName(), e);
		}
	}

}
