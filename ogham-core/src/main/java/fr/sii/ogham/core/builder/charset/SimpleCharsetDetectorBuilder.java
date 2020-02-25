package fr.sii.ogham.core.builder.charset;

import java.nio.charset.Charset;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.core.charset.FixedCharsetDetector;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.fluent.AbstractParent;

public class SimpleCharsetDetectorBuilder<P> extends AbstractParent<P> implements CharsetDetectorBuilder<P> {
	private EnvironmentBuilder<?> environmentBuilder;
	private final ConfigurationValueBuilderHelper<SimpleCharsetDetectorBuilder<P>, String> charsetValueBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public SimpleCharsetDetectorBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		charsetValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
	}

	@Override
	public CharsetDetectorBuilder<P> defaultCharset(String charsetName) {
		charsetValueBuilder.setValue(charsetName);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<CharsetDetectorBuilder<P>, String> defaultCharset() {
		return new ConfigurationValueBuilderDelegate<>(this, charsetValueBuilder);
	}

	@Override
	public CharsetDetector build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		return new FixedCharsetDetector(getDefaultCharset(propertyResolver, charsetValueBuilder));
	}

	private static Charset getDefaultCharset(PropertyResolver propertyResolver, ConfigurationValueBuilderHelper<?, String> valueBuilder) {
		String charset = valueBuilder.getValue(propertyResolver);
		if (charset != null) {
			return Charset.forName(charset);
		}
		return Charset.defaultCharset();
	}

}
