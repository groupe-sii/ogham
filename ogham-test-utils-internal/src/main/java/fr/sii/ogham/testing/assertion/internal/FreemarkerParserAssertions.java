package fr.sii.ogham.testing.assertion.internal;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.testing.util.HasParent;
import freemarker.template.Configuration;

/**
 * Helper to make assertions on {@link FreeMarkerParser}.
 * 
 * @author Aurélien Baudet
 *
 */
public class FreemarkerParserAssertions extends HasParent<FreemarkerAssersions> {
	private final Set<FreeMarkerParser> parsers;

	public FreemarkerParserAssertions(FreemarkerAssersions parent, Set<FreeMarkerParser> parsers) {
		super(parent);
		this.parsers = parsers;
	}

	/**
	 * Make assertions on FreeMarker {@link Configuration} instance to ensure
	 * that it is correctly configured.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * configuration()
	 *   .defaultEncoding(equalTo("UTF-8"))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public FreemarkerConfigurationAssersions configuration() {
		return new FreemarkerConfigurationAssersions(this, parsers.stream().map(FreemarkerParserAssertions::getConfiguration).collect(toSet()));
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
	public ResourceResolverAssertions<FreemarkerParserAssertions> resourceResolver() {
		return new ResourceResolverAssertions<>(this, getResourceResolvers());
	}
	
	private Set<ResourceResolver> getResourceResolvers() {
		return parsers.stream()
				.map(FreemarkerParserAssertions::getResourceResolver)
				.collect(Collectors.toSet());
	}


	private static Configuration getConfiguration(FreeMarkerParser freeMarkerParser) {
		try {
			return (Configuration) readField(freeMarkerParser, "configuration", true);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to read 'configuration' field of FreeMarkerParser", e);
		}
	}

	private static ResourceResolver getResourceResolver(FreeMarkerParser parser) {
		try {
			return (ResourceResolver) FieldUtils.readField(parser, "resolver", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'resolver' field of FreeMarkerParser", e);
		}
	}
}
