package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_ISO_8859_1;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_PACKED_GSM;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_8BIT;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_DEFAULT;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_LATIN1;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_UCS2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;

/**
 * Provide a Data Coding Scheme according to charset used to encode the message.
 * The resulting encoding is <b>Character Encoding Group</b>:
 * 
 * Bit 7 6 5 4 3 2 1 0<br>
 * <br>
 * Bits 7..4 contain the "Coding Group Bits" which control what values are
 * contained in bits 3..0 OR even 5..0<br>
 * <br>
 * <b>0000: Character Encoding Group</b>
 * <ul>
 * <li>Bits 0,1,2,3 Represent 16 Language Encodings</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class CharsetMapToCharacterEncodingGroupDataCodingProvider implements DataCodingProvider {
	private final Map<String, Byte> alphabetIndexedByCharsetName;

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
	 * <li>{@link CharsetUtil#NAME_ISO_8859_1} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_LATIN1}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 */
	public CharsetMapToCharacterEncodingGroupDataCodingProvider() {
		super();
		this.alphabetIndexedByCharsetName = defaultMap();
	}

	@Override
	public DataCoding provide(Encoded encoded) {
		NamedCharset charset = NamedCharset.from(encoded.getCharsetName());
		return DataCoding.createCharacterEncodingGroup(alphabetIndexedByCharsetName.get(charset.getCharsetName()));
	}

	/**
	 * Default mapping used to determine {@link DataCoding} encoding value from
	 * {@link Charset}:
	 * <ul>
	 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->} {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_8BIT}</li>
	 * <li>{@link CharsetUtil#NAME_ISO_8859_1} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_LATIN1}</li>
	 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
	 * {@link DataCoding#CHAR_ENC_UCS2}</li>
	 * </ul>
	 * 
	 * 
	 * @return the mapping
	 */
	public static Map<String, Byte> defaultMap() {
		Map<String, Byte> map = new HashMap<>();
		// @formatter:off
		map.put(NAME_GSM, 			CHAR_ENC_8BIT);
		map.put(NAME_GSM8, 			CHAR_ENC_8BIT);
		// SMPP v3.3 defines 0 for GSM 7-bit packed.
		// Since SMPP v3.4, meaning of DCS=0 is ambiguous...
		// But it is the only possible value for GSM 7-bit packed 
		map.put(NAME_GSM7, 			CHAR_ENC_DEFAULT);
		map.put(NAME_PACKED_GSM, 	CHAR_ENC_DEFAULT);
		map.put(NAME_ISO_8859_1, 	CHAR_ENC_LATIN1);
		map.put(NAME_UCS_2, 		CHAR_ENC_UCS2);
		// TODO: if other charset are used => which data coding values to use ? Where to find the information ???
//		map.put(CharsetUtil.NAME_AIRWIDE_GSM, DataCoding.);
//		map.put(CharsetUtil.NAME_AIRWIDE_IA5, DataCoding.);
//		map.put(CharsetUtil.NAME_ISO_8859_15, DataCoding.);
//		map.put(CharsetUtil.NAME_MODIFIED_UTF8, DataCoding.);
//		map.put(CharsetUtil.NAME_TMOBILENL_GSM, DataCoding.);
//		map.put(CharsetUtil.NAME_UTF_8, DataCoding.);
//		map.put(CharsetUtil.NAME_VFD2_GSM, DataCoding.);
//		map.put(CharsetUtil.NAME_VFTR_GSM, DataCoding.);

		
//		map.put(CharsetUtil.NAME_GSM, 		DataCoding.CHAR_ENC_8BIT);
//		map.put(CharsetUtil.NAME_GSM8, 		DataCoding.CHAR_ENC_8BIT);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_8BITA);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_CYRLLIC);
//		map.put(CharsetUtil.NAME_GSM7, 		DataCoding.CHAR_ENC_DEFAULT);
//		map.put(CharsetUtil.NAME_PACKED_GSM, 		DataCoding.CHAR_ENC_DEFAULT);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_EXKANJI);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_HEBREW);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_IA5);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_JIS);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_KSC5601);
//		map.put(CharsetUtil.NAME_ISO_8859_1, 		DataCoding.CHAR_ENC_LATIN1);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_MUSIC);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_PICTO);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_RSRVD);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_RSRVD2);
//		map.put(CharsetUtil., 		DataCoding.CHAR_ENC_RSRVD3);
//		map.put(CharsetUtil.NAME_UCS_2, 		DataCoding.CHAR_ENC_UCS2);
		// @formatter:on
		return Collections.unmodifiableMap(map);
	}

}
