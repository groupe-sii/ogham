package fr.sii.ogham.sms.splitter;

import static java.util.Arrays.asList;

import java.util.List;

import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.exception.message.SplitMessageException;

/**
 * A no-op splitter that never splits any message.
 * 
 * <p>
 * It always returns a single segment with the whole message.
 * 
 * <p>
 * The message is encoded using the provided {@link Encoder}. The
 * {@link Encoder} may be null and the result segment wraps the unencoded
 * original string.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoSplitMessageSplitter implements MessageSplitter {
	private final Encoder encoder;

	/**
	 * Initializes with no encoder. The result of split is one segment with
	 * unencoded original message.
	 * 
	 */
	public NoSplitMessageSplitter() {
		this(null);
	}

	/**
	 * Initializes with the {@link Encoder} used to encode the string message
	 * into a byte array.
	 * 
	 * <p>
	 * The encoder may be null, resulting in a single segment with the unencoded
	 * original string.
	 * 
	 * @param encoder
	 *            the encoder used to encode the message
	 */
	public NoSplitMessageSplitter(Encoder encoder) {
		super();
		this.encoder = encoder;
	}

	@Override
	public List<Segment> split(String message) throws SplitMessageException {
		if (encoder == null) {
			return asList(new StringSegment(message));
		}
		try {
			return asList(new EncodedSegment(encoder.encode(message)));
		} catch (EncodingException e) {
			throw new SplitMessageException("Message couldn't be encoded", message, e);
		}
	}

}
