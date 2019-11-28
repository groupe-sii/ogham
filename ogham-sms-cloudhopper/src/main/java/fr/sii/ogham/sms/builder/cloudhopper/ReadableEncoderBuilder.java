package fr.sii.ogham.sms.builder.cloudhopper;

import static java.util.Arrays.asList;

import java.util.List;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.splitter.MessageSplitter;

/**
 * Encoding configuration is shared between {@link CloudhopperBuilder} and
 * {@link MessageSplitterBuilder}. {@link CloudhopperBuilder} directly uses the
 * result of {@link EncoderBuilder#build()}. However,
 * {@link MessageSplitterBuilder} can't use result of
 * {@link EncoderBuilder#build()} directly but only the registered properties
 * and values to create an {@link Encoder} dedicated to a
 * {@link MessageSplitter}.
 * 
 * {@link EncoderBuilder} is the part that is "visible" by the developer and
 * developer should not access methods used to get values (internal methods),
 * just methods to configure like with other builders in order to have a simple
 * contract.
 * 
 * This class uses package protected visibility to access configured values.
 * 
 * @author Aurélien Baudet
 *
 */
public class ReadableEncoderBuilder {
	private EncoderBuilder delegate;

	/**
	 * Set the encoder builder that is configured by the developer.
	 * 
	 * @param encoderBuilder
	 *            the encoder builder
	 */
	public void update(EncoderBuilder encoderBuilder) {
		this.delegate = encoderBuilder;
	}

	/**
	 * @return the registered properties/values for GSM 7-bit encoding
	 */
	public List<String> getGsm7Priorities() {
		if (delegate == null) {
			return asList();
		}
		return delegate.gsm7Packed.getProperties();
	}

	/**
	 * @return the registered properties/values for GSM 8-bit encoding
	 */
	public List<String> getGsm8Priorities() {
		if (delegate == null) {
			return asList();
		}
		return delegate.gsm8.getProperties();
	}

	/**
	 * @return the registered properties/values for UCS-2 encoding
	 */
	public List<String> getUcs2Priorities() {
		if (delegate == null) {
			return asList();
		}
		return delegate.ucs2.getProperties();
	}

	/**
	 * @return the registered properties/values for Latin 1 encoding
	 */
	public List<String> getLatin1Priorities() {
		if (delegate == null) {
			return asList();
		}
		return delegate.latin1.getProperties();
	}

	/**
	 * @param propertyResolver
	 *            property resolver used to evaluate property values
	 * @return true if automatic guessing has been enabled
	 */
	public boolean autoGuessEnabled(PropertyResolver propertyResolver) {
		if (delegate == null) {
			return false;
		}
		return delegate.autoGuessEnabled(propertyResolver);
	}
}
