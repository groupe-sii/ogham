package fr.sii.ogham.helper.sms.util;

import java.util.Arrays;

import org.jsmpp.bean.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;

import fr.sii.ogham.helper.sms.bean.OptionalParameter;
import fr.sii.ogham.helper.sms.bean.SubmitSm;
import fr.sii.ogham.helper.sms.bean.Tag;

/**
 * Utility class that is smart enough to find which field was used to send the
 * message. It is also able to determine if a User Data Header was used and
 * extract real payload. It uses the right decoder to decode the message into a
 * string.
 * 
 * @author Aurélien Baudet
 *
 */
public final class MessageDecoder {
	private static final Logger LOG = LoggerFactory.getLogger(MessageDecoder.class);

	/**
	 * Decode the received message to extract the text message.
	 * 
	 * @param submitSm
	 *            the message to decode
	 * @return the text message
	 */
	public static String decode(SubmitSm submitSm) {
		byte[] shortMessage = getMessageBytes(submitSm);
		if (shortMessage == null) {
			return null;
		}
		if (submitSm.isUdhi()) {
			int headerLength = shortMessage[0] + 1;
			shortMessage = Arrays.copyOfRange(shortMessage, headerLength, shortMessage.length);
		}
		Alphabet alphabet = Alphabet.parseDataCoding(submitSm.getDataCoding());
		Charset charset = getCharset(alphabet);
		LOG.trace("alphabet={}, charset={}, isUdhi={}, header={}", alphabet, charset, submitSm.isUdhi(), shortMessage);
		return charset.decode(shortMessage);
	}

	private static byte[] getMessageBytes(SubmitSm submitSm) {
		byte[] shortMessage = submitSm.getShortMessage();
		if (shortMessage != null && shortMessage.length > 0) {
			return shortMessage;
		}
		OptionalParameter parameter = submitSm.getOptionalParameter(Tag.MESSAGE_PAYLOAD);
		if (parameter != null) {
			return parameter.getValue();
		}
		return null;
	}

	private static Charset getCharset(Alphabet alphabet) {
		// @formatter:off
		switch (alphabet) {
			case ALPHA_DEFAULT:						return CharsetUtil.CHARSET_GSM7;
			case ALPHA_8_BIT:						return CharsetUtil.CHARSET_GSM8;
			case ALPHA_UCS2:						return CharsetUtil.CHARSET_UCS_2;
			case ALPHA_LATIN1:						return CharsetUtil.CHARSET_ISO_8859_1;
//			case ALPHA_IA5:							return CharsetUtil.;
//			case ALPHA_CYRILLIC:					return CharsetUtil.;
//			case ALPHA_ISO_2022_JP_MUSIC_CODES:		return CharsetUtil.;
//			case ALPHA_JIS:							return CharsetUtil.;
//			case ALPHA_JIS_X_0212_1990:				return CharsetUtil.;
//			case ALPHA_KS_C_5601:					return CharsetUtil.;
//			case ALPHA_LATIN_HEBREW:				return CharsetUtil.;
//			case ALPHA_PICTOGRAM_ENCODING:			return CharsetUtil.;
			default:
				throw new IllegalStateException("The alphabet " + alphabet.name() + " can't be decoded because no charset implementation can handle it");
		}
		// @formatter:on
	}

	private MessageDecoder() {
		super();
	}
}
