package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_PACKED_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_8BIT;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_DEFAULT;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_UCS2;
import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.UnsupportedCharsetException;

/**
 * Provide a Data Coding Scheme according to charset used to encode the message.
 * The resulting encoding is <b>General Data Coding Group</b>:
 * 
 * <ul>
 * <li>If Bits 4 {@literal &} 5 are 0 {@literal &} 0 then see above "0000
 * Character Encoding Group"</li>
 * <li>Message Class (Bit 0 {@literal &} 1) (default 0, 0)</li>
 * <li>Alphabet (Bit 2 {@literal &} 3) (default 0, 0)</li>
 * <li>Message Class Present (Bit 4) (whether bits 0 {@literal &} 1 have
 * meaning)</li>
 * <li>Compression Flag (Bit 5) (0 - uncompressed, 1 - compressed)</li>
 * </ul>
 * 
 * 
 * <strong>WARNING:</strong> This provider only supports GSM 7-bit, GSM 8-bit
 * and UCS-2 charsets. This is due to use of
 * {@link DataCoding#createGeneralGroup(byte, Byte, boolean)} that only accepts
 * {@link DataCoding#CHAR_ENC_DEFAULT}, {@link DataCoding#CHAR_ENC_8BIT},
 * {@link DataCoding#CHAR_ENC_UCS2}.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class CharsetMapToGeneralGroupDataCodingProvider implements DataCodingProvider {
	private final boolean failIfUnknown;
	private final Map<String, Byte> alphabetIndexedByCharsetName;
	private final Byte messageClass;
	private final boolean compressed;

	/**
	 * Provides {@link DataCoding} based on the charset used to encode the
	 * message.
	 * 
	 * <p>
	 * The default map is used (charset name {@literal ->} alphabet):
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * 
	 * <p>
	 * The message class is set to {@code null}.
	 * 
	 * <p>
	 * The compressed field is set to false.
	 * 
	 * @param failIfUnknown
	 *            if true it throws {@link UnsupportedCharsetException}, if
	 *            false is returns null to let other
	 *            {@link DataCodingProvider}(s) being executed.
	 * 
	 */
	public CharsetMapToGeneralGroupDataCodingProvider(boolean failIfUnknown) {
		this(failIfUnknown, defaultMap());
	}

	/**
	 * Provides {@link DataCoding} based on the charset used to encode the
	 * message.
	 * 
	 * <p>
	 * It uses the provided map to determine alphabet from the charset used to
	 * encode the message.
	 * 
	 * <p>
	 * The message class is set to {@code null}.
	 * 
	 * <p>
	 * The compressed field is set to false.
	 * 
	 * 
	 * 
	 * @param failIfUnknown
	 *            if true it throws {@link UnsupportedCharsetException}, if
	 *            false is returns null to let other
	 *            {@link DataCodingProvider}(s) being executed.
	 * @param alphabetIndexedByCharsetName
	 *            the map used to determine Data Coding Alphabet from charset
	 *            name
	 */
	public CharsetMapToGeneralGroupDataCodingProvider(boolean failIfUnknown, Map<String, Byte> alphabetIndexedByCharsetName) {
		this(failIfUnknown, alphabetIndexedByCharsetName, null, false);
	}

	/**
	 * Provides {@link DataCoding} based on the charset used to encode the
	 * message.
	 * 
	 * <p>
	 * It uses the provided map to determine alphabet from the charset used to
	 * encode the message.
	 * 
	 * <p>
	 * It uses the provided message class to use for the Data Coding scheme. The
	 * Message Class bit and Message Class Present bit are both determined from
	 * the provided class. If {@code null}, the "message class" not active flag
	 * will not be set.
	 * 
	 * <p>
	 * It uses the provided compressed value to indicate if the message is
	 * compressed or not.
	 * 
	 * 
	 * 
	 * @param failIfUnknown
	 *            if true it throws {@link UnsupportedCharsetException}, if
	 *            false is returns null to let other
	 *            {@link DataCodingProvider}(s) being executed.
	 * @param alphabetIndexedByCharsetName
	 *            the map used to determine Data Coding Alphabet from charset
	 *            name
	 * @param messageClass
	 *            the message class value to use
	 * @param compressed
	 *            indicate if message is compressed or not
	 */
	public CharsetMapToGeneralGroupDataCodingProvider(boolean failIfUnknown, Map<String, Byte> alphabetIndexedByCharsetName, Byte messageClass, boolean compressed) {
		super();
		this.failIfUnknown = failIfUnknown;
		this.alphabetIndexedByCharsetName = alphabetIndexedByCharsetName;
		this.messageClass = messageClass;
		this.compressed = compressed;
	}

	@Override
	public DataCoding provide(Encoded encoded) throws DataCodingException {
		NamedCharset charset = NamedCharset.from(encoded.getCharsetName());
		Byte encoding = alphabetIndexedByCharsetName.get(charset.getCharsetName());
		if (encoding == null) {
			if (failIfUnknown) {
				throw new UnsupportedCharsetException(encoded.getCharsetName() + " charset not supported for General Group Data Coding Scheme", encoded);
			}
			return null;
		}
		return DataCoding.createGeneralGroup(encoding, messageClass, compressed);
	}

	/**
	 * Default mapping used to determine {@link DataCoding} encoding value from
	 * {@link Charset}:
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * @return the mapping
	 */
	public static Map<String, Byte> defaultMap() {
		Map<String, Byte> map = new HashMap<>();
		// @formatter:off
		// standards only are supported (due to DataCoding.createGeneralGroup)
		map.put(NAME_GSM7, 			CHAR_ENC_DEFAULT);
		map.put(NAME_PACKED_GSM, 	CHAR_ENC_DEFAULT);
		map.put(NAME_GSM, 			CHAR_ENC_8BIT);
		map.put(NAME_GSM8, 			CHAR_ENC_8BIT);
		map.put(NAME_UCS_2, 		CHAR_ENC_UCS2);
		// @formatter:on
		return unmodifiableMap(map);
	}

}
