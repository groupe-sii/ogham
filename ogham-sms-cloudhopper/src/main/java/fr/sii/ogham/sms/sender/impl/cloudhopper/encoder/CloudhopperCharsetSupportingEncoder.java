package fr.sii.ogham.sms.sender.impl.cloudhopper.encoder;

import com.cloudhopper.commons.charset.Charset;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.encoder.EncodedMessage;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.encoder.SupportingEncoder;
import fr.sii.ogham.sms.exception.message.EncodingException;

/**
 * An {@link Encoder} that checks if the message can be encoded with a
 * particular {@link Charset}.
 * 
 * <p>
 * To test if message can be encoded by the provided {@link Charset}, the
 * message is first encoded into a byte array and then the byte array is decoded
 * into a string (using {@link Charset#normalize(CharSequence)} method). If the
 * result of normalization has not the same length, it means that the
 * {@link Charset} can't handle the message properly.
 * 
 * <p>
 * Moreover, {@link Charset} implementations use '?' character if a character of
 * original string can't be properly converted. Therefore, the number of '?'
 * characters are count before and after normalization. If the number is not the
 * same, it means that the {@link Charset} can't handle the message properly.
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperCharsetSupportingEncoder implements SupportingEncoder {
	private final NamedCharset charset;

	/**
	 * Initializes with the charset used to encode the message if it can.
	 * 
	 * @param charset
	 *            the charset to use
	 */
	public CloudhopperCharsetSupportingEncoder(NamedCharset charset) {
		super();
		this.charset = charset;
	}

	@Override
	public Encoded encode(String message) throws EncodingException {
		return new EncodedMessage(message, charset.getCharset().encode(message), charset.getCharsetName());
	}

	@Override
	public boolean canEncode(String message) {
		String normalized = charset.getCharset().normalize(message);
		if (normalized.length() > message.length()) {
			return false;
		}
		return countQuestionMarks(message) == countQuestionMarks(normalized);
	}

	private static long countQuestionMarks(String str) {
		return str.chars().filter(ch -> ch == '?').count();
	}
}
