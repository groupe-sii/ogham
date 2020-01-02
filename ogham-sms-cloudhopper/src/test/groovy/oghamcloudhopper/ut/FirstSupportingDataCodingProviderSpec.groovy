package oghamcloudhopper.ut;

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2

import com.cloudhopper.commons.gsm.DataCoding

import fr.sii.ogham.sms.encoder.Encoded
import fr.sii.ogham.sms.encoder.Encoder
import fr.sii.ogham.sms.encoder.SupportingEncoder
import fr.sii.ogham.sms.exception.message.EncodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.GuessEncodingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.GuessEncodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.DataCodingProvider
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.FirstSupportingDataCodingProvider
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class FirstSupportingDataCodingProviderSpec extends Specification {
	
	def "#dcs1 | #dcs2 should return #expected"() {
		given:
			DataCodingProvider provider1 = Mock()
			DataCodingProvider provider2 = Mock()
			provider1.provide(_) >> { dcs1 == null ? null : DataCoding.parse(dcs1) }
			provider2.provide(_) >> { dcs2 == null ? null : DataCoding.parse(dcs2) }
			Encoded encoded = Mock()
			def provider = new FirstSupportingDataCodingProvider([provider1, provider2])

		when:
			def dcs = provider.provide(encoded)

		then:
			dcs.getByteValue() == expected

		where:
			dcs1		| dcs2		|| expected
			null		| (byte) 0	|| (byte) 0
			(byte) 0	| null		|| (byte) 0
			(byte) 0	| (byte) 5	|| (byte) 0
			(byte) 10	| null		|| (byte) 10
			(byte) 10	| (byte) 5	|| (byte) 10
	}
	
	def "empty provider list should always fail"() {
		given:
			Encoded encoded = Mock()
			def provider = new FirstSupportingDataCodingProvider()

		when:
			provider.provide(encoded)

		then:
			def e = thrown(DataCodingException)
			e.getMessage() == "No DataCodingProvider could determine a valid Data Coding Scheme"
	}

	def "all providers return null should fail"() {
		given:
			DataCodingProvider provider1 = Mock()
			DataCodingProvider provider2 = Mock()
			provider1.provide(_) >> null
			provider2.provide(_) >> null
			Encoded encoded = Mock()
			def provider = new FirstSupportingDataCodingProvider()
			provider.register(provider1)
			provider.register(provider2)

		when:
			provider.provide(encoded)

		then:
			def e = thrown(DataCodingException)
			e.getMessage() == "No DataCodingProvider could determine a valid Data Coding Scheme"
	}


	def "should fail with #expectedMessage"() {
		given:
			DataCodingProvider provider1 = Mock()
			provider1.provide(_) >> { throw exception }
			Encoded encoded = Mock()
			def provider = new FirstSupportingDataCodingProvider(provider1)

		when:
			provider.provide(encoded)

		then:
			def e = thrown(expected)
			e.getMessage() == expectedMessage

		where:
			exception								|| expected					| expectedMessage
			new DataCodingException("foo", null)	|| DataCodingException		| "foo"
			new IllegalArgumentException("illegal")	|| IllegalArgumentException	| "illegal"
	}


}
