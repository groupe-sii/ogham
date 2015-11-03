package fr.sii.ogham.sms.builder.cloudhopper;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.charset.FixedCharsetProvider;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperCharsetHandler;
import fr.sii.ogham.sms.sender.impl.cloudhopper.MapCloudhopperCharsetHandler;

public class CharsetBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<CloudhopperCharsetHandler> {
	private EnvironmentBuilder<?> environmentBuilder;
	private List<String> charsets;
	private List<CharsetMapping> mappings;
	
	public CharsetBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		charsets = new ArrayList<>();
		mappings = new ArrayList<>();
	}

	public CharsetBuilder defaultCharset(String... charsets) {
		this.charsets.addAll(Arrays.asList(charsets));
		return this;
	}
	
	public CharsetBuilder convert(String nioCharsetName, String cloudhopperCharset) {
		mappings.add(new CharsetMapping(nioCharsetName, cloudhopperCharset));
		return this;
	}

	@Override
	public CloudhopperCharsetHandler build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		FixedCharsetProvider defaultCharsetProvider = new FixedCharsetProvider(getDefaultCharset(propertyResolver, charsets));
		MapCloudhopperCharsetHandler charsetHandler = new MapCloudhopperCharsetHandler(defaultCharsetProvider);
		for(CharsetMapping mapping : mappings) {
			try {
				charsetHandler.addCharset(mapping.getNioCharset(), mapping.getCloudhopperCharset());
			} catch (EncodingException e) {
				throw new BuildException("Unable to build default charset handler", e);
			}
		}
		return charsetHandler;
	}
	
	private Charset getDefaultCharset(PropertyResolver propertyResolver, List<String> charsets) {
		String charset = BuilderUtils.evaluate(charsets, propertyResolver, String.class);
		if(charset!=null) {
			return Charset.forName(charset);
		}
		return Charset.defaultCharset();
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
