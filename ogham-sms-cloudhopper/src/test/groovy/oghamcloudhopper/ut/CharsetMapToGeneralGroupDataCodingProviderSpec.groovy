package oghamcloudhopper.ut;

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_PACKED_GSM
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_8BIT
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_DEFAULT
import static com.cloudhopper.commons.gsm.DataCoding.CHAR_ENC_UCS2
import static com.cloudhopper.commons.gsm.DataCoding.MESSAGE_CLASS_0
import static com.cloudhopper.commons.gsm.DataCoding.MESSAGE_CLASS_1
import static com.cloudhopper.commons.gsm.DataCoding.MESSAGE_CLASS_2
import static com.cloudhopper.commons.gsm.DataCoding.MESSAGE_CLASS_3
import static fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.CharsetMapToGeneralGroupDataCodingProvider.defaultMap

import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.commons.gsm.DataCoding
import com.cloudhopper.commons.gsm.DataCoding.Group

import fr.sii.ogham.sms.encoder.Encoded
import fr.sii.ogham.sms.encoder.Encoder
import fr.sii.ogham.sms.encoder.SupportingEncoder
import fr.sii.ogham.sms.exception.message.EncodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.GuessEncodingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.GuessEncodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.UnsupportedCharsetException
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.CharsetMapToGeneralGroupDataCodingProvider
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.DataCodingProvider
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.FirstSupportingDataCodingProvider
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class CharsetMapToGeneralGroupDataCodingProviderSpec extends Specification {
	
	def "#charset compressed=#compressed and message class=#messageClass should return #expected"() {
		given:
			Encoded encoded = Mock()
			encoded.getCharsetName() >> charset
			def provider = new CharsetMapToGeneralGroupDataCodingProvider(false, defaultMap(), messageClass, compressed)

		when:
			def dcs = provider.provide(encoded)

		then:
			dcs.getCodingGroup() == Group.GENERAL
			dcs.getMessageClass() == expectedMessageClass
			dcs.isCompressed() == compressed
			dcs.getCharacterEncoding() == expectedEncoding
			dcs.getByteValue() == expected
			
		where:
			charset				| compressed	| messageClass		|| expected 	| expectedMessageClass 	| expectedEncoding
			NAME_GSM7			| false			| null				|| (byte) 0 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_GSM7			| false			| MESSAGE_CLASS_0	|| (byte) 16	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_GSM7			| false			| MESSAGE_CLASS_1	|| (byte) 17 	| MESSAGE_CLASS_1		| CHAR_ENC_DEFAULT
			NAME_GSM7			| false			| MESSAGE_CLASS_2	|| (byte) 18 	| MESSAGE_CLASS_2		| CHAR_ENC_DEFAULT
			NAME_GSM7			| false			| MESSAGE_CLASS_3	|| (byte) 19 	| MESSAGE_CLASS_3		| CHAR_ENC_DEFAULT
			NAME_GSM7			| true			| null				|| (byte) 32 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_GSM7			| true			| MESSAGE_CLASS_0	|| (byte) 48 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_GSM7			| true			| MESSAGE_CLASS_1	|| (byte) 49 	| MESSAGE_CLASS_1		| CHAR_ENC_DEFAULT
			NAME_GSM7			| true			| MESSAGE_CLASS_2	|| (byte) 50 	| MESSAGE_CLASS_2		| CHAR_ENC_DEFAULT
			NAME_GSM7			| true			| MESSAGE_CLASS_3	|| (byte) 51 	| MESSAGE_CLASS_3		| CHAR_ENC_DEFAULT

			NAME_PACKED_GSM		| false			| null				|| (byte) 0 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| false			| MESSAGE_CLASS_0	|| (byte) 16 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| false			| MESSAGE_CLASS_1	|| (byte) 17 	| MESSAGE_CLASS_1		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| false			| MESSAGE_CLASS_2	|| (byte) 18 	| MESSAGE_CLASS_2		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| false			| MESSAGE_CLASS_3	|| (byte) 19 	| MESSAGE_CLASS_3		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| true			| null				|| (byte) 32 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| true			| MESSAGE_CLASS_0	|| (byte) 48 	| MESSAGE_CLASS_0		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| true			| MESSAGE_CLASS_1	|| (byte) 49 	| MESSAGE_CLASS_1		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| true			| MESSAGE_CLASS_2	|| (byte) 50 	| MESSAGE_CLASS_2		| CHAR_ENC_DEFAULT
			NAME_PACKED_GSM		| true			| MESSAGE_CLASS_3	|| (byte) 51 	| MESSAGE_CLASS_3		| CHAR_ENC_DEFAULT

			NAME_GSM8			| false			| null				|| (byte) 4 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM8			| false			| MESSAGE_CLASS_0	|| (byte) 20 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM8			| false			| MESSAGE_CLASS_1	|| (byte) 21 	| MESSAGE_CLASS_1		| CHAR_ENC_8BIT
			NAME_GSM8			| false			| MESSAGE_CLASS_2	|| (byte) 22	| MESSAGE_CLASS_2		| CHAR_ENC_8BIT
			NAME_GSM8			| false			| MESSAGE_CLASS_3	|| (byte) 23 	| MESSAGE_CLASS_3		| CHAR_ENC_8BIT
			NAME_GSM8			| true			| null				|| (byte) 36 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM8			| true			| MESSAGE_CLASS_0	|| (byte) 52 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM8			| true			| MESSAGE_CLASS_1	|| (byte) 53	| MESSAGE_CLASS_1		| CHAR_ENC_8BIT
			NAME_GSM8			| true			| MESSAGE_CLASS_2	|| (byte) 54 	| MESSAGE_CLASS_2		| CHAR_ENC_8BIT
			NAME_GSM8			| true			| MESSAGE_CLASS_3	|| (byte) 55 	| MESSAGE_CLASS_3		| CHAR_ENC_8BIT
			
			NAME_GSM			| false			| null				|| (byte) 4 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM			| false			| MESSAGE_CLASS_0	|| (byte) 20 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM			| false			| MESSAGE_CLASS_1	|| (byte) 21	| MESSAGE_CLASS_1		| CHAR_ENC_8BIT
			NAME_GSM			| false			| MESSAGE_CLASS_2	|| (byte) 22 	| MESSAGE_CLASS_2		| CHAR_ENC_8BIT
			NAME_GSM			| false			| MESSAGE_CLASS_3	|| (byte) 23 	| MESSAGE_CLASS_3		| CHAR_ENC_8BIT
			NAME_GSM			| true			| null				|| (byte) 36 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM			| true			| MESSAGE_CLASS_0	|| (byte) 52 	| MESSAGE_CLASS_0		| CHAR_ENC_8BIT
			NAME_GSM			| true			| MESSAGE_CLASS_1	|| (byte) 53 	| MESSAGE_CLASS_1		| CHAR_ENC_8BIT
			NAME_GSM			| true			| MESSAGE_CLASS_2	|| (byte) 54 	| MESSAGE_CLASS_2		| CHAR_ENC_8BIT
			NAME_GSM			| true			| MESSAGE_CLASS_3	|| (byte) 55 	| MESSAGE_CLASS_3		| CHAR_ENC_8BIT

			NAME_UCS_2			| false			| null				|| (byte) 8 	| MESSAGE_CLASS_0		| CHAR_ENC_UCS2
			NAME_UCS_2			| false			| MESSAGE_CLASS_0	|| (byte) 24 	| MESSAGE_CLASS_0		| CHAR_ENC_UCS2
			NAME_UCS_2			| false			| MESSAGE_CLASS_1	|| (byte) 25 	| MESSAGE_CLASS_1		| CHAR_ENC_UCS2
			NAME_UCS_2			| false			| MESSAGE_CLASS_2	|| (byte) 26 	| MESSAGE_CLASS_2		| CHAR_ENC_UCS2
			NAME_UCS_2			| false			| MESSAGE_CLASS_3	|| (byte) 27 	| MESSAGE_CLASS_3		| CHAR_ENC_UCS2
			NAME_UCS_2			| true			| null				|| (byte) 40 	| MESSAGE_CLASS_0		| CHAR_ENC_UCS2
			NAME_UCS_2			| true			| MESSAGE_CLASS_0	|| (byte) 56 	| MESSAGE_CLASS_0		| CHAR_ENC_UCS2
			NAME_UCS_2			| true			| MESSAGE_CLASS_1	|| (byte) 57 	| MESSAGE_CLASS_1		| CHAR_ENC_UCS2
			NAME_UCS_2			| true			| MESSAGE_CLASS_2	|| (byte) 58 	| MESSAGE_CLASS_2		| CHAR_ENC_UCS2
			NAME_UCS_2			| true			| MESSAGE_CLASS_3	|| (byte) 59 	| MESSAGE_CLASS_3		| CHAR_ENC_UCS2
	}
	
	def "unsupported charset #charset"() {
		given:
			Encoded encoded = Mock()
			encoded.getCharsetName() >> charset
			def provider = new CharsetMapToGeneralGroupDataCodingProvider(true)

		when:
			def dcs = provider.provide(encoded)

		then:
			thrown(UnsupportedCharsetException)
			
		where:
			charset << [
				CharsetUtil.NAME_AIRWIDE_GSM,
				CharsetUtil.NAME_AIRWIDE_IA5,
				CharsetUtil.NAME_ISO_8859_1,
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
			def provider = new CharsetMapToGeneralGroupDataCodingProvider(false)

		when:
			def dcs = provider.provide(encoded)

		then:
			dcs == null
			
		where:
			charset << [
				CharsetUtil.NAME_AIRWIDE_GSM,
				CharsetUtil.NAME_AIRWIDE_IA5,
				CharsetUtil.NAME_ISO_8859_1,
				CharsetUtil.NAME_ISO_8859_15,
				CharsetUtil.NAME_MODIFIED_UTF8,
				CharsetUtil.NAME_TMOBILENL_GSM,
				CharsetUtil.NAME_UTF_8,
				CharsetUtil.NAME_VFD2_GSM,
				CharsetUtil.NAME_VFTR_GSM
			]
	}
}
