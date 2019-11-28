package fr.sii.ogham.sms.sender.impl.cloudhopper.splitter;

import java.util.List;

import fr.sii.ogham.sms.encoder.SupportingEncoder;
import fr.sii.ogham.sms.exception.message.SplitMessageException;
import fr.sii.ogham.sms.splitter.MessageSplitter;
import fr.sii.ogham.sms.splitter.Segment;
import fr.sii.ogham.sms.splitter.SupportingSplitter;

/**
 * A splitter that indicates if it is able to split the message based on the
 * result of {@link SupportingEncoder#canEncode(String)} of the associated
 * encoder.
 * 
 * @author Aurélien Baudet
 *
 */
public class SupportedEncoderConditionalSplitter implements SupportingSplitter {
	private final SupportingEncoder associatedEncoder;
	private final MessageSplitter delegate;

	/**
	 * Initializes with the encoder that will encode the string message into a
	 * byte array. The encoder implements {@link SupportingEncoder} so it can
	 * indicate if it is able to encode the message or not.
	 * 
	 * @param associatedEncoder
	 *            the encoder used to encode the message if it can handle it
	 * @param delegate
	 *            the splitter that will really split the message
	 */
	public SupportedEncoderConditionalSplitter(SupportingEncoder associatedEncoder, MessageSplitter delegate) {
		super();
		this.associatedEncoder = associatedEncoder;
		this.delegate = delegate;
	}

	@SuppressWarnings("squid:S1168")
	@Override
	public List<Segment> split(String message) throws SplitMessageException {
		if (canSplit(message)) {
			return delegate.split(message);
		}
		return null;
	}

	@Override
	public boolean canSplit(String message) {
		return associatedEncoder.canEncode(message);
	}

}
