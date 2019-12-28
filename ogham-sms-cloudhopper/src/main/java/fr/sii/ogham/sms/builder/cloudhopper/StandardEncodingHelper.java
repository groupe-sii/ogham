package fr.sii.ogham.sms.builder.cloudhopper;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;

/**
 * Helper class that extends {@link ConfigurationValueBuilderHelper} to track
 * the associated charset name.
 * 
 * @author Aurélien Baudet
 *
 */
class StandardEncodingHelper extends ConfigurationValueBuilderHelper<EncoderBuilder, Integer> {
	private final String charsetName;

	public StandardEncodingHelper(EncoderBuilder parent, String charsetName) {
		super(parent, Integer.class);
		this.charsetName = charsetName;
	}

	public NamedCharset getCharset() {
		return NamedCharset.from(charsetName);
	}
}