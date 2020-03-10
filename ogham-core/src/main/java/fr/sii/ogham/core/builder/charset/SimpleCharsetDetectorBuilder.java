package fr.sii.ogham.core.builder.charset;

import java.nio.charset.Charset;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.core.charset.FixedCharsetDetector;
import fr.sii.ogham.core.fluent.AbstractParent;

public class SimpleCharsetDetectorBuilder<P> extends AbstractParent<P> implements CharsetDetectorBuilder<P> {
	private final ConfigurationValueBuilderHelper<SimpleCharsetDetectorBuilder<P>, String> charsetValueBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public SimpleCharsetDetectorBuilder(P parent, BuildContext buildContext) {
		super(parent);
		charsetValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
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
		return new FixedCharsetDetector(getDefaultCharset(charsetValueBuilder));
	}

	private static Charset getDefaultCharset(ConfigurationValueBuilderHelper<?, String> valueBuilder) {
		String charset = valueBuilder.getValue();
		if (charset != null) {
			return Charset.forName(charset);
		}
		return Charset.defaultCharset();
	}

}
