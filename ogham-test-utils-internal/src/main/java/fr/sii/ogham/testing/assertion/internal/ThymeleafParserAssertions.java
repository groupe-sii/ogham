package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.AssertionHelper.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Matcher;
import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafParser;
import fr.sii.ogham.testing.assertion.HasParent;

/**
 * Helper class to make assertions on {@link ThymeleafParser} instance created
 * by Ogham.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafParserAssertions extends HasParent<ThymeleafAssertions> {
	private final Set<ThymeleafParser> parsers;

	public ThymeleafParserAssertions(ThymeleafAssertions parent, Set<ThymeleafParser> parsers) {
		super(parent);
		this.parsers = parsers;
	}

	/**
	 * Ensures that {@link TemplateEngine} is configured as expected.
	 * 
	 * @param <T>
	 *            the type of expected object
	 * @param matcher
	 *            the matcher to ensure that engine is configured as expected.
	 * @return this instance for fluent chaining
	 */
	public <T extends TemplateEngine> ThymeleafParserAssertions engine(Matcher<? super TemplateEngine> matcher) {
		for (ThymeleafParser thymeleafParser : parsers) {
			assertThat(getEngine(thymeleafParser), matcher);
		}
		return this;
	}

	/**
	 * Make assertions on resource resolution.
	 * 
	 * <pre>
	 * {@code
	 * resourceResolver()
	 *   .classpath()
	 *     .pathPrefix(is("prefix/")
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public ResourceResolverAssertions<ThymeleafParserAssertions> resourceResolver() {
		return new ResourceResolverAssertions<>(this, getResourceResolvers());
	}

	private Set<ResourceResolver> getResourceResolvers() {
		return parsers.stream()
				.map(ThymeleafParserAssertions::getResourceResolver)
				.collect(Collectors.toSet());
	}

	private static TemplateEngine getEngine(ThymeleafParser parser) {
		try {
			return (TemplateEngine) FieldUtils.readField(parser, "engine", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'engine' field of ThymeleafParser", e);
		}
	}

	private static ResourceResolver getResourceResolver(ThymeleafParser parser) {
		try {
			return (ResourceResolver) FieldUtils.readField(parser, "resolver", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'resolver' field of ThymeleafParser", e);
		}
	}
}
