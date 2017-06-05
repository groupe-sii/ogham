package fr.sii.ogham.core.builder.charset;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.core.charset.FixedCharsetDetector;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;

public class SimpleCharsetDetectorBuilder<P> extends AbstractParent<P> implements CharsetDetectorBuilder<P> {
	private EnvironmentBuilder<?> environmentBuilder;
	private final List<String> charsets;

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
		charsets = new ArrayList<>();
	}

	@Override
	public CharsetDetectorBuilder<P> defaultCharset(String... charsets) {
		this.charsets.addAll(Arrays.asList(charsets));
		return this;
	}

	@Override
	public CharsetDetector build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		return new FixedCharsetDetector(getDefaultCharset(propertyResolver, charsets));
	}

	private Charset getDefaultCharset(PropertyResolver propertyResolver, List<String> charsets) {
		String charset = BuilderUtils.evaluate(charsets, propertyResolver, String.class);
		if (charset != null) {
			return Charset.forName(charset);
		}
		return Charset.defaultCharset();
	}

}
