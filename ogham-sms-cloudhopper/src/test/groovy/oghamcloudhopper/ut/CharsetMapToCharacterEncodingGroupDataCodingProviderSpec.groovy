package oghamcloudhopper.ut;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_ISO_8859_1
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_PACKED_GSM
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_8BIT
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_DEFAULT
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_LATIN1
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_UCS2
import static com.cloudhopper.commons.gsm.DataCoding.MESSAGE_CLASS_0

import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.commons.gsm.DataCoding.Group

import fr.sii.ogham.sms.encoder.Encoded
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.UnsupportedCharsetException
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.CharsetMapToCharacterEncodingGroupDataCodingProvider
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class CharsetMapToCharacterEncodingGroupDataCodingProviderSpec extends Specification {
	
	def "#charset should return #expected"() {
		given:
			Encoded encoded = Mock()
			encoded.getCharsetName() >> charset
			def provider = new CharsetMapToCharacterEncodingGroupDataCodingProvider(false)

		when:
			def dcs = provider.provide(encoded)

		then:
			dcs.getCodingGroup() == Group.CHARACTER_ENCODING
			dcs.getMessageClass() == MESSAGE_CLASS_0
			dcs.isCompressed() == false
			dcs.getCharacterEncoding() == expectedEncoding
			dcs.getByteValue() == expected
			
		where:
			charset				|| expected 	| expectedEncoding
			NAME_GSM7			|| (byte) 0 	| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		|| (byte) 0 	| CHAR_ENC_DEFAULT
			NAME_GSM			|| (byte) 4 	| CHAR_ENC_8BIT
			NAME_GSM8			|| (byte) 4 	| CHAR_ENC_8BIT
			NAME_ISO_8859_1		|| (byte) 3 	| CHAR_ENC_LATIN1
			NAME_UCS_2			|| (byte) 8		| CHAR_ENC_UCS2
	}
	
	def "unsupported charset #charset"() {
		given:
			Encoded encoded = Mock()
			encoded.getCharsetName() >> charset
			def provider = new CharsetMapToCharacterEncodingGroupDataCodingProvider(true)

		when:
			def dcs = provider.provide(encoded)

		then:
			thrown(UnsupportedCharsetException)
			
		where:
			charset << [
				CharsetUtil.NAME_AIRWIDE_GSM,
				CharsetUtil.NAME_AIRWIDE_IA5,
				CharsetUtil.NAME_ISO_8859_15,
				CharsetUtil.NAME_MODIFIED_UTF8,
				CharsetUtil.NAME_TMOBILENL_GSM,
				CharsetUtil.NAME_UTF_8,
				CharsetUtil.NAME_VFD2_GSM,
				CharsetUtil.NAME_VFTR_GSM
			]
	}
	
	def "unsupported charset #charset skipped"() {
		given:
			Encoded encoded = Mock()
			encoded.getCharsetName() >> charset
			def provider = new CharsetMapToCharacterEncodingGroupDataCodingProvider(false)

		when:
			def dcs = provider.provide(encoded)

		then:
			dcs == null
			
		where:
			charset << [
				CharsetUtil.NAME_AIRWIDE_GSM,
				CharsetUtil.NAME_AIRWIDE_IA5,
				CharsetUtil.NAME_ISO_8859_15,
				CharsetUtil.NAME_MODIFIED_UTF8,
				CharsetUtil.NAME_TMOBILENL_GSM,
				CharsetUtil.NAME_UTF_8,
				CharsetUtil.NAME_VFD2_GSM,
				CharsetUtil.NAME_VFTR_GSM
			]
	}
}
