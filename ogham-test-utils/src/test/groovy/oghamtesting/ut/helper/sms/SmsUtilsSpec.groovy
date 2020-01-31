package oghamtesting.ut.helper.sms

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2

import com.cloudhopper.commons.charset.CharsetUtil

import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.sms.simulator.bean.OptionalParameter
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm
import fr.sii.ogham.testing.sms.simulator.bean.Tag
import fr.sii.ogham.testing.sms.simulator.decode.SmsUtils
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class SmsUtilsSpec extends Specification {
	def "'#shortMessage' should be decoded as '#expected' using short message field"() {
		given:
			SubmitSm submit = Mock()
			submit.isUdhi() >> udhi
			submit.getDataCoding() >> dataCoding
			List shortMessageBytes = encoding.encode(shortMessage)
			submit.getShortMessage() >> ((header + shortMessageBytes) as byte[])
		
		when:
			def text = SmsUtils.getSmsContent(submit)
		
		then:
			expected == text
		
		where:
			shortMessage				| encoding			| header						| udhi		| dataCoding	|| expected
			""							| CHARSET_GSM7		| []							| false		| 0				|| null
			"foo bar"					| CHARSET_GSM7		| []							| false		| 0				|| "foo bar"
			"a"*48						| CHARSET_GSM7		| [0x05, 0, 0x03, 0x25, 2, 1]	| true		| 0				|| "a"*48
			""							| CHARSET_GSM8		| []							| false		| 4				|| null
			"foo bar"					| CHARSET_GSM8		| []							| false		| 4				|| "foo bar"
			"a"*48						| CHARSET_GSM8		| [0x05, 0, 0x03, 0x25, 2, 1]	| true		| 4				|| "a"*48
			""							| CHARSET_UCS_2		| []							| false		| 8				|| null
			"foo bar"					| CHARSET_UCS_2		| []							| false		| 8				|| "foo bar"
			"a"*48						| CHARSET_UCS_2		| [0x05, 0, 0x03, 0x25, 2, 1]	| true		| 8				|| "a"*48
	}
	
	def "'#message' should be decoded as '#expected' using 'message_payload' parameter"() {
		given:
			SubmitSm submit = Mock()
			submit.isUdhi() >> udhi
			submit.getDataCoding() >> dataCoding
			List messageBytes = encoding.encode(message)
			OptionalParameter param = Mock()
			param.getLength() >> (header + messageBytes).size()
			param.getTag() >> Tag.MESSAGE_PAYLOAD
			param.getValue() >> ((header + messageBytes) as byte[])
			submit.getOptionalParameter(Tag.MESSAGE_PAYLOAD) >> param
		
		when:
			def text = SmsUtils.getSmsContent(submit)
		
		then:
			expected == text
		
		where:
			message						| encoding			| header						| udhi		| dataCoding	|| expected
			""							| CHARSET_GSM7		| []							| false		| 0				|| ""
			"foo bar"					| CHARSET_GSM7		| []							| false		| 0				|| "foo bar"
			"a"*48						| CHARSET_GSM7		| [0x05, 0, 0x03, 0x25, 2, 1]	| true		| 0				|| "a"*48
			""							| CHARSET_GSM8		| []							| false		| 4				|| ""
			"foo bar"					| CHARSET_GSM8		| []							| false		| 4				|| "foo bar"
			"a"*48						| CHARSET_GSM8		| [0x05, 0, 0x03, 0x25, 2, 1]	| true		| 4				|| "a"*48
			""							| CHARSET_UCS_2		| []							| false		| 8				|| ""
			"foo bar"					| CHARSET_UCS_2		| []							| false		| 8				|| "foo bar"
			"a"*48						| CHARSET_UCS_2		| [0x05, 0, 0x03, 0x25, 2, 1]	| true		| 8				|| "a"*48
	}
}
