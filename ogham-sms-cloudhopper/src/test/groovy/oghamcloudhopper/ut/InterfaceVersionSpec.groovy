package oghamcloudhopper.ut;

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2

import com.cloudhopper.smpp.SmppConstants

import fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.GuessEncodingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class InterfaceVersionSpec extends Specification {
	
	def "'#version' should be #expected"() {
		when:
			def interfaceVersion = InterfaceVersion.of(version)

		then:
			interfaceVersion == expected

		where:
			version					|| expected
			"VERSION_3_3"			|| InterfaceVersion.VERSION_3_3
			"VERSION_3_4"			|| InterfaceVersion.VERSION_3_4
			"VERSION_5_0"			|| InterfaceVersion.VERSION_5_0
			"3.3"					|| InterfaceVersion.VERSION_3_3
			"3.4"					|| InterfaceVersion.VERSION_3_4
			"5.0"					|| InterfaceVersion.VERSION_5_0
			"5"						|| InterfaceVersion.VERSION_5_0
			// null and empty string allowed to allow a default value
			null					|| null
			""						|| null
	}
	
	def "#version is invalid"() {
		when:
			InterfaceVersion.of(version)

		then:
			thrown(IllegalArgumentException)

		where:
			version << ["VERSION_3", "3"]
	}

	def "#value value should be #expected"() {
		when:
			def interfaceVersion = InterfaceVersion.fromValue(value)

		then:
			interfaceVersion == expected

		where:
			value						|| expected
			SmppConstants.VERSION_3_3	|| InterfaceVersion.VERSION_3_3
			SmppConstants.VERSION_3_4	|| InterfaceVersion.VERSION_3_4
			SmppConstants.VERSION_5_0	|| InterfaceVersion.VERSION_5_0
			// null and empty string allowed to allow a default value
			null						|| null
	}

	
	def "#value number is invalid"() {
		when:
			InterfaceVersion.fromValue((byte) value)

		then:
			thrown(IllegalArgumentException)

		where:
			value << [0, 20]
	}
}
