package fr.sii.ogham.sms.builder.smsglobal;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_DEFAULT;
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_UCS2;
import static com.cloudhopper.commons.gsm.DataCoding.createCharacterEncodingGroup;

import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.DataCodingProvider;

/**
 * SMSGlobal only supports either GSM 8-bit or UCS-2 encodings.
 * 
 * <p>
 * If GSM 8-bit encoding is used, DCS value is set to 0
 * </p>
 * 
 * <p>
 * If UCS-2 encoding is used, DCS value is set to 8
 * </p>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class SmsGlobalDataCodingProvider implements DataCodingProvider {

	@Override
	public DataCoding provide(Encoded encoded) throws DataCodingException {
		if (NAME_UCS_2.equals(encoded.getCharsetName())) {
			return createCharacterEncodingGroup(CHAR_ENC_UCS2);
		}
		return createCharacterEncodingGroup(CHAR_ENC_DEFAULT);
	}
	
}