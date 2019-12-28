package fr.sii.ogham.assertion.internal;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;

import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Matcher;
import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafParser;

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

	private static TemplateEngine getEngine(ThymeleafParser parser) {
		try {
			return (TemplateEngine) FieldUtils.readField(parser, "engine", true);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to get 'engine' field of ThymeleafParser", e);
		}
	}
}
