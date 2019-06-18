package fr.sii.ogham.assertion.internal;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.util.Set;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
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
		return new FreemarkerConfigurationAssersions(this, parsers.stream().map(this::getConfiguration).collect(toSet()));
	}

	private Configuration getConfiguration(FreeMarkerParser freeMarkerParser) {
		try {
			return (Configuration) readField(freeMarkerParser, "configuration", true);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to read 'configuration' field of FreeMarkerParser", e);
		}
	}
}
