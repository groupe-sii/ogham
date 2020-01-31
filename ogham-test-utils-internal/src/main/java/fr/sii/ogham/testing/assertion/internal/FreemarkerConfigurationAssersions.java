package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;

import java.util.Set;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.util.HasParent;
import freemarker.template.Configuration;

/**
 * Helper to make assertions on FreeMarker {@link Configuration}.
 * 
 * @author Aurélien Baudet
 *
 */
public class FreemarkerConfigurationAssersions extends HasParent<FreemarkerParserAssertions> {
	private final Set<Configuration> configurations;

	public FreemarkerConfigurationAssersions(FreemarkerParserAssertions parent, Set<Configuration> configurations) {
		super(parent);
		this.configurations = configurations;
	}

	/**
	 * Ensures that default encoding used by particular FreeMarker
	 * {@link Configuration} is correctly set.
	 * 
	 * @param matcher
	 *            the matcher to ensure that default encoding is correct.
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationAssersions defaultEncoding(Matcher<String> matcher) {
		for (Configuration configuration : configurations) {
			assertThat(configuration.getDefaultEncoding(), matcher);
		}
		return this;
	}
}
