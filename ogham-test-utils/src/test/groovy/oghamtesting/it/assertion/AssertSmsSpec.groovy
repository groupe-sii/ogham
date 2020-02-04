package oghamtesting.it.assertion

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertAll
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat
import static fr.sii.ogham.testing.sms.simulator.bean.Alphabet.ALPHA_8_BIT
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.DATA
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ERMES
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.NATIONAL
import static fr.sii.ogham.testing.sms.simulator.bean.Tag.CALLBACK_NUM
import static fr.sii.ogham.testing.sms.simulator.bean.Tag.NUMBER_OF_MESSAGES
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.ABBREVIATED
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.ALPHANUMERIC
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.INTERNATIONAL
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.Matchers.array
import static org.hamcrest.Matchers.arrayWithSize
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.hasProperty
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.lessThan
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static org.hamcrest.Matchers.startsWith

import java.util.function.Consumer

import org.jsmpp.bean.DataCoding
import org.jsmpp.bean.OptionalParameter
import org.jsmpp.bean.OptionalParameter.Number_of_messages
import org.jsmpp.bean.OptionalParameter.Tag
import org.junit.ComparisonFailure

import com.cloudhopper.commons.charset.CharsetUtil

import fr.sii.ogham.testing.assertion.sms.AssertSms
import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber
import fr.sii.ogham.testing.assertion.sms.ExpectedSms
import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.extension.junit.SmppServerRule
import fr.sii.ogham.testing.sms.simulator.bean.Address
import fr.sii.ogham.testing.sms.simulator.bean.Alphabet
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber
import fr.sii.ogham.testing.sms.simulator.decode.CloudhopperCharsetAdapter
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertSmsSpec extends Specification {
	def setupSpec() {
		System.setProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "false");
	}
	
	def cleanupSpec() {
		System.clearProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure");
	}

	def "assertEquals(ExpectedSms) #desc"() {
		given:
			SubmitSm sms = Mock()
			
			sms.getShortMessage() >> CHARSET_GSM8.encode("message")
			sms.getDataCoding() >> ALPHA_8_BIT.value()
			Address sender = Mock()
			sms.getSourceAddress() >> sender
			sender.getAddress() >> "0102030405"
			sender.getNpi() >> NATIONAL.value()
			sender.getTon() >> INTERNATIONAL.value()
			Address receiver = Mock()
			sms.getDestAddress() >> receiver
			receiver.getAddress() >> "0605040302"
			receiver.getNpi() >> DATA.value()
			receiver.getTon() >> ALPHANUMERIC.value()

			def expectedSms = new ExpectedSms(message, 
				new ExpectedAddressedPhoneNumber(senderNumber, senderTon.value(), senderNpi.value()), 
				new ExpectedAddressedPhoneNumber(receiverNumber, receiverTon.value(), receiverNpi.value()))
		
		when:
			def failures = collectFailures { AssertSms.assertEquals(expectedSms, sms) }
		
		then:
			failures == expected
			
		where:
			desc					| message		| senderNumber	| senderTon		| senderNpi	| receiverNumber	| receiverTon	| receiverNpi	|| expected
			"should pass"			| "message"		| "0102030405"	| INTERNATIONAL	| NATIONAL	| "0605040302"		| ALPHANUMERIC	| DATA			|| []
			"should detect all"		| "foo"			| "0000000000"	| ABBREVIATED	| ISDN		| "1111111111"		| UNKNOWN		| ERMES			|| [
				 [klass: ComparisonFailure, message: 'Sender number should be 0000000000 expected:<0[000000000]> but was:<0[102030405]>'], 
				 [klass: AssertionError, message: 'Sender ton should be 6 expected:<6> but was:<1>'], 
				 [klass: AssertionError, message: 'Sender npi should be 1 expected:<1> but was:<8>'], 
				 [klass: ComparisonFailure, message: 'Receiver number should be 1111111111 expected:<[1111111111]> but was:<[0605040302]>'], 
				 [klass: AssertionError, message: 'Receiver ton should be 0 expected:<0> but was:<5>'], 
				 [klass: AssertionError, message: 'Receiver npi should be 10 expected:<10> but was:<3>'], 
				 [klass: ComparisonFailure, message: 'Message not consistent with expected expected:<[foo]> but was:<[message]>']
				]
	}
	
	def "assertEquals(ExpectedSms, #desc) should not throw NPE and provide understandable message"() {
		given:
			def expectedSms = new ExpectedSms("message", 
				new ExpectedAddressedPhoneNumber("senderNumber", INTERNATIONAL, NATIONAL), 
				new ExpectedAddressedPhoneNumber("receiverNumber", ALPHANUMERIC, DATA))
			
		when:
			def failures = collectFailures {
				AssertSms.assertEquals(expectedSms, actual());
			}
		
		then:
			failures == expected
		
		where:
			desc			| actual				|| expected
			"null message"	| { [null] }				|| [
					 [klass: AssertionError, message: 'Sender number should be senderNumber expected:<senderNumber> but was:<null>'], 
					 [klass: AssertionError, message: 'Sender ton should be 1 expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'Sender npi should be 8 expected:<8> but was:<null>'], 
					 [klass: AssertionError, message: 'Receiver number should be receiverNumber expected:<receiverNumber> but was:<null>'], 
					 [klass: AssertionError, message: 'Receiver ton should be 5 expected:<5> but was:<null>'], 
					 [klass: AssertionError, message: 'Receiver npi should be 3 expected:<3> but was:<null>'], 
					 [klass: AssertionError, message: 'Message not consistent with expected expected:<message> but was:<null>']
					]
			"empty mock"	| { [Mock(SubmitSm)] }	|| [
					 [klass: AssertionError, message: 'Sender number should be senderNumber expected:<senderNumber> but was:<null>'], 
					 [klass: AssertionError, message: 'Sender ton should be 1 expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'Sender npi should be 8 expected:<8> but was:<null>'], 
					 [klass: AssertionError, message: 'Receiver number should be receiverNumber expected:<receiverNumber> but was:<null>'], 
					 [klass: AssertionError, message: 'Receiver ton should be 5 expected:<5> but was:<null>'], 
					 [klass: AssertionError, message: 'Receiver npi should be 3 expected:<3> but was:<null>'], 
					 [klass: AssertionError, message: 'Message not consistent with expected expected:<message> but was:<null>']
					]
	}


	private List collectFailures(Closure cl) {
		try {
			cl()
			return []
		} catch(MultipleAssertionError e) {
			return failures(e)
		}
	}
	
	private List failures(MultipleAssertionError e) {
		def assertions = []
		for (Throwable t : e.getFailures()) {
			assertions += [klass: t.getClass(), message: t.getMessage().replaceAll("\\s+", " ")];
		}
		return assertions
	}
}
