package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_ISO_8859_1;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;

import fr.sii.ogham.core.builder.BuildContext;
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
	private final BuildContext buildContext;
	private EncoderBuilder delegate;

	/**
	 * Initialize with the build context
	 * 
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public ReadableEncoderBuilder(BuildContext buildContext) {
		super();
		this.buildContext = buildContext;
	}

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
	public StandardEncodingHelper getGsm7Priorities() {
		if (delegate == null) {
			return new StandardEncodingHelper(delegate, NAME_GSM7, buildContext);
		}
		return delegate.gsm7PackedValueBuilder;
	}

	/**
	 * @return the registered properties/values for GSM 8-bit encoding
	 */
	public StandardEncodingHelper getGsm8Priorities() {
		if (delegate == null) {
			return new StandardEncodingHelper(delegate, NAME_GSM, buildContext);
		}
		return delegate.gsm8ValueBuilder;
	}

	/**
	 * @return the registered properties/values for UCS-2 encoding
	 */
	public StandardEncodingHelper getUcs2Priorities() {
		if (delegate == null) {
			return new StandardEncodingHelper(delegate, NAME_UCS_2, buildContext);
		}
		return delegate.ucs2ValueBuilder;
	}

	/**
	 * @return the registered properties/values for Latin 1 encoding
	 */
	public StandardEncodingHelper getLatin1Priorities() {
		if (delegate == null) {
			return new StandardEncodingHelper(delegate, NAME_ISO_8859_1, buildContext);
		}
		return delegate.latin1ValueBuilder;
	}

	/**
	 * @return true if automatic guessing has been enabled
	 */
	public boolean autoGuessEnabled() {
		if (delegate == null) {
			return false;
		}
		return delegate.autoGuessEnabled();
	}
}
