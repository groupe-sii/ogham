package fr.sii.ogham.sms.sender.impl.cloudhopper.encoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.encoder.SupportingEncoder;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.GuessEncodingException;

/**
 * Automatically determine best {@link Encoder} to use for encoding the message.
 * 
 * <p>
 * The guessing is really simple:
 * <ol>
 * <li>The first {@link Encoder} is tested to check if message can be encoded
 * with that encoder:
 * <ul>
 * <li>If the {@link Encoder} implements {@link SupportingEncoder}, the
 * {@link SupportingEncoder#canEncode(String)} is called to check if the
 * {@link Encoder} is able to encode the message. If false is returned, the
 * {@link Encoder} is skipped (not executed).</li>
 * <li>If the {@link Encoder} doesn't implement {@link SupportingEncoder}, the
 * {@link Encoder} is executed. The {@link Encoder} may throw an
 * {@link EncodingException} if it can't encode correctly the message. If that
 * exception is raised, it is caught and the {@link Encoder} is skipped.</li>
 * <li>If the {@link Encoder} is not skipped (it could encode the message
 * correctly), its result is directly returned.</li>
 * </ul>
 * <li>The next {@link Encoder} is tested and so on until one {@link Encoder}
 * returns the encoded message</li>
 * </ol>
 * 
 * <p>
 * If none of the possible encoders could encode the message, the
 * {@link GuessEncodingException} is raised.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class GuessEncodingEncoder implements SupportingEncoder {
	private static final Logger LOG = LoggerFactory.getLogger(GuessEncodingEncoder.class);

	private final List<Encoder> possibleEncoders;

	/**
	 * Initializes with the list of possible encoders to test. If an encoder
	 * implements {@link SupportingEncoder}, the
	 * {@link SupportingEncoder#canEncode(String)} is used to check if the text
	 * message can be encoded using that {@link Encoder}. Otherwise, the
	 * {@link Encoder} is executed to try encoding the message. If the message
	 * can't be encoded, an {@link EncodingException} is raised. The exception
	 * is caught and the next {@link Encoder} is executed.
	 * 
	 * @param possibleEncoders
	 *            the possible encoders used to encode the message
	 */
	public GuessEncodingEncoder(List<Encoder> possibleEncoders) {
		super();
		this.possibleEncoders = possibleEncoders;
	}

	@Override
	public boolean canEncode(String message) {
		for (Encoder encoder : possibleEncoders) {
			if (canEncode(encoder, message)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Encoded encode(String message) throws EncodingException {
		for (Encoder encoder : possibleEncoders) {
			if (!canEncode(encoder, message)) {
				continue;
			}
			try {
				return encoder.encode(message);
			} catch (EncodingException e) {
				LOG.debug("Encoder failed to encode => try next one", e);
			}
		}
		throw new GuessEncodingException("Failed to guess encoding for message", message);
	}

	private static boolean canEncode(Encoder encoder, String message) {
		if (encoder instanceof SupportingEncoder) {
			return ((SupportingEncoder) encoder).canEncode(message);
		}
		return true;
	}
}
