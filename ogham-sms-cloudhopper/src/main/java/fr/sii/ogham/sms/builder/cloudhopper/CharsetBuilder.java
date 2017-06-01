package fr.sii.ogham.sms.builder.cloudhopper;

import static fr.sii.ogham.core.util.BuilderUtils.evaluate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.charset.CharsetDetectorBuilder;
import fr.sii.ogham.core.builder.charset.SimpleCharsetDetectorBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperCharsetHandler;
import fr.sii.ogham.sms.sender.impl.cloudhopper.MapCloudhopperCharsetHandler;

/**
 * Configures how Cloudhopper will handle charset encoding for SMS messages.
 * Charsets defined by the SMPP protocol may be different from NIO charsets.
 * 
 * This builder configures detection of the NIO charset defined by the SMS
 * content handle by the Java application.
 * 
 * This builder also configures how conversion from NIO charset to SMPP charset
 * is handled.
 * 
 * @author Aurélien Baudet
 *
 */
public class CharsetBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<CloudhopperCharsetHandler> {
	private EnvironmentBuilder<?> environmentBuilder;
	private List<CharsetMapping> mappings;
	private SimpleCharsetDetectorBuilder<CharsetBuilder> charsetDetectorBuilder;

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
	public CharsetBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		mappings = new ArrayList<>();
	}

	/**
	 * Configures NIO charset detection (see {@link Charset}). Charset detection
	 * is needed to detect the encoding of the SMS content used by the Java
	 * application. This encoding will then be used to be converted by a charset
	 * that is supported by the SMPP protocol.
	 * 
	 * @return the builder to configure charset detection
	 */
	public CharsetDetectorBuilder<CharsetBuilder> detector() {
		if (charsetDetectorBuilder == null) {
			charsetDetectorBuilder = new SimpleCharsetDetectorBuilder<>(this, environmentBuilder);
		}
		return charsetDetectorBuilder;
	}

	/**
	 * Registers a charset conversion. Conversion is required by Cloudhopper in
	 * order to use a charset supported by the SMPP protocol.
	 * 
	 * You can register several charset conversions.
	 * 
	 * @param nioCharsetName
	 *            the charset used by the Java application
	 * @param cloudhopperCharset
	 *            the charset supported by the SMPP protocol
	 * @return this instance for fluent chaining
	 */
	public CharsetBuilder convert(String nioCharsetName, String cloudhopperCharset) {
		mappings.add(new CharsetMapping(nioCharsetName, cloudhopperCharset));
		return this;
	}

	@Override
	public CloudhopperCharsetHandler build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		CharsetDetector charsetDetector = charsetDetectorBuilder.build();
		MapCloudhopperCharsetHandler charsetHandler = new MapCloudhopperCharsetHandler(charsetDetector);
		List<String> registered = new ArrayList<>();
		for (CharsetMapping mapping : mappings) {
			addCharset(propertyResolver, charsetHandler, mapping, registered);
		}
		return charsetHandler;
	}

	private void addCharset(PropertyResolver propertyResolver, MapCloudhopperCharsetHandler charsetHandler, CharsetMapping mapping, List<String> registered) {
		try {
			String nioCharset = evaluate(mapping.getNioCharset(), propertyResolver, String.class);
			String cloudhopperCharset = evaluate(mapping.getCloudhopperCharset(), propertyResolver, String.class);
			if (nioCharset != null && cloudhopperCharset != null && !registered.contains(nioCharset)) {
				charsetHandler.addCharset(nioCharset, cloudhopperCharset);
				registered.add(nioCharset);
			}
		} catch (EncodingException e) {
			throw new BuildException("Unable to build default charset handler", e);
		}
	}

	private static class CharsetMapping {
		private final String nioCharset;
		private final String cloudhopperCharset;

		public CharsetMapping(String nioCharset, String cloudhopperCharset) {
			super();
			this.nioCharset = nioCharset;
			this.cloudhopperCharset = cloudhopperCharset;
		}

		public String getNioCharset() {
			return nioCharset;
		}

		public String getCloudhopperCharset() {
			return cloudhopperCharset;
		}
	}
}
