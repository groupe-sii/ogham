package oghamtesting.it.assertion

import org.junit.jupiter.api.Assertions

import static ogham.testing.com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static fr.sii.ogham.testing.sms.simulator.bean.Alphabet.ALPHA_8_BIT
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.DATA
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ERMES
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.NATIONAL
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.ABBREVIATED
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.ALPHANUMERIC
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.INTERNATIONAL
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN

import org.opentest4j.AssertionFailedError

import fr.sii.ogham.testing.assertion.sms.AssertSms
import fr.sii.ogham.testing.assertion.sms.ExpectedAddressedPhoneNumber
import fr.sii.ogham.testing.assertion.sms.ExpectedSms
import fr.sii.ogham.testing.assertion.sms.SplitSms
import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.sms.simulator.bean.Address
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm
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
			SubmitSm sms = Mock {
				Address sender = Mock {
					getAddress() >> "0102030405"
					getNpi() >> NATIONAL.value()
					getTon() >> INTERNATIONAL.value()
				}
				Address receiver = Mock {
					getAddress() >> "0605040302"
					getNpi() >> DATA.value()
					getTon() >> ALPHANUMERIC.value()
				}
				getShortMessage() >> CHARSET_GSM8.encode("message")
				getDataCoding() >> ALPHA_8_BIT.value()
				getSourceAddress() >> sender
				getDestAddress() >> receiver
			}
			

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
				 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be 0000000000 ==> expected: <0000000000> but was: <0102030405>'], 
				 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 6 ==> expected: <6> but was: <1>'],
				 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 1 ==> expected: <1> but was: <8>'],
				 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be 1111111111 ==> expected: <1111111111> but was: <0605040302>'],
				 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 0 ==> expected: <0> but was: <5>'],
				 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 10 ==> expected: <10> but was: <3>'],
				 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <foo> but was: <message>']
				]
	}
	
	def "assertEquals(ExpectedSms, #desc) should not throw NPE and provide understandable message"() {
		given:
			def expectedSms = new ExpectedSms("message", 
				new ExpectedAddressedPhoneNumber("senderNumber", INTERNATIONAL, NATIONAL), 
				new ExpectedAddressedPhoneNumber("receiverNumber", ALPHANUMERIC, DATA))
			
		when:
			def failures = collectFailures(
				{ AssertSms.assertEquals(expectedSms, actual()) },
				{ AssertSms.assertEquals([expectedSms], actual()) }
			)
		
		then:
			failures == expected
		
		where:
			desc					| actual								|| expected
			"null message"			| { [null] }							|| [
					 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be senderNumber ==> expected: <senderNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 1 ==> expected: <1> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 8 ==> expected: <8> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be receiverNumber ==> expected: <receiverNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 5 ==> expected: <5> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 3 ==> expected: <3> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <message> but was: <null>']
					] * 2
			"empty mock"			| { [Mock(SubmitSm)] }					|| [
					 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be senderNumber ==> expected: <senderNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 1 ==> expected: <1> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 8 ==> expected: <8> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be receiverNumber ==> expected: <receiverNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 5 ==> expected: <5> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 3 ==> expected: <3> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <message> but was: <null>']
					] * 2
			"no message"			| { [] }								|| [
					 [klass: AssertionFailedError, message: 'should have received exactly one message ==> expected: <1> but was: <0>'],
					 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be senderNumber ==> expected: <senderNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 1 ==> expected: <1> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 8 ==> expected: <8> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be receiverNumber ==> expected: <receiverNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 5 ==> expected: <5> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 3 ==> expected: <3> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <message> but was: <null>'],
					 [klass: AssertionFailedError, message: 'should have received exactly 1 message(s) ==> expected: <1> but was: <0>'],
					 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be senderNumber ==> expected: <senderNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 1 ==> expected: <1> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 8 ==> expected: <8> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be receiverNumber ==> expected: <receiverNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 5 ==> expected: <5> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 3 ==> expected: <3> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <message> but was: <null>']
					]
			"too many messages"		| { [Mock(SubmitSm), Mock(SubmitSm)] }	|| [
					 [klass: AssertionFailedError, message: 'should have received exactly one message ==> expected: <1> but was: <2>'],
					 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be senderNumber ==> expected: <senderNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 1 ==> expected: <1> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 8 ==> expected: <8> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be receiverNumber ==> expected: <receiverNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 5 ==> expected: <5> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 3 ==> expected: <3> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <message> but was: <null>'],
					 [klass: AssertionFailedError, message: 'should have received exactly 1 message(s) ==> expected: <1> but was: <2>'],
					 [klass: AssertionFailedError, message: 'Sender number of message 1/1 should be senderNumber ==> expected: <senderNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender ton of message 1/1 should be 1 ==> expected: <1> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Sender npi of message 1/1 should be 8 ==> expected: <8> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver number of message 1/1 should be receiverNumber ==> expected: <receiverNumber> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver ton of message 1/1 should be 5 ==> expected: <5> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Receiver npi of message 1/1 should be 3 ==> expected: <3> but was: <null>'], 
					 [klass: AssertionFailedError, message: 'Message 1/1 not consistent with expected ==> expected: <message> but was: <null>']
					]
	}

	def "assertEquals(SplitSms) #desc"() {
		given:
			Address sender = Mock {
				getAddress() >> "0102030405"
				getNpi() >> NATIONAL.value()
				getTon() >> INTERNATIONAL.value()
			}
			Address receiver = Mock {
				getAddress() >> "0605040302"
				getNpi() >> DATA.value()
				getTon() >> ALPHANUMERIC.value()
			}

			SubmitSm sms1 = Mock {
				getShortMessage() >> CHARSET_GSM8.encode("part1")
				getDataCoding() >> ALPHA_8_BIT.value()
				getSourceAddress() >> sender
				getDestAddress() >> receiver
			}
			
			SubmitSm sms2 = Mock {
				getShortMessage() >> CHARSET_GSM8.encode("part2")
				getDataCoding() >> ALPHA_8_BIT.value()
				getSourceAddress() >> sender
				getDestAddress() >> receiver
			}


			def expectedSms = new SplitSms(
				new ExpectedAddressedPhoneNumber(senderNumber, senderTon.value(), senderNpi.value()), 
				new ExpectedAddressedPhoneNumber(receiverNumber, receiverTon.value(), receiverNpi.value()),
				messages as String[])
		
		when:
			def failures = collectFailures { AssertSms.assertEquals(expectedSms, [sms1, sms2]) }
		
		then:
			failures == expected
			
		where:
			desc					| messages						| senderNumber	| senderTon		| senderNpi	| receiverNumber	| receiverTon	| receiverNpi	|| expected
			"should pass"			| ["part1", "part2"]			| "0102030405"	| INTERNATIONAL	| NATIONAL	| "0605040302"		| ALPHANUMERIC	| DATA			|| []
			"should detect all"		| ["foo", "bar"]				| "0000000000"	| ABBREVIATED	| ISDN		| "1111111111"		| UNKNOWN		| ERMES			|| [
				 [klass: AssertionFailedError, message: 'Sender number of message 1/2 should be 0000000000 ==> expected: <0000000000> but was: <0102030405>'],
				 [klass: AssertionFailedError, message: 'Sender ton of message 1/2 should be 6 ==> expected: <6> but was: <1>'],
				 [klass: AssertionFailedError, message: 'Sender npi of message 1/2 should be 1 ==> expected: <1> but was: <8>'],
				 [klass: AssertionFailedError, message: 'Receiver number of message 1/2 should be 1111111111 ==> expected: <1111111111> but was: <0605040302>'],
				 [klass: AssertionFailedError, message: 'Receiver ton of message 1/2 should be 0 ==> expected: <0> but was: <5>'],
				 [klass: AssertionFailedError, message: 'Receiver npi of message 1/2 should be 10 ==> expected: <10> but was: <3>'],
				 [klass: AssertionFailedError, message: 'Message 1/2 not consistent with expected ==> expected: <foo> but was: <part1>'],
				 [klass: AssertionFailedError, message: 'Sender number of message 2/2 should be 0000000000 ==> expected: <0000000000> but was: <0102030405>'],
				 [klass: AssertionFailedError, message: 'Sender ton of message 2/2 should be 6 ==> expected: <6> but was: <1>'],
				 [klass: AssertionFailedError, message: 'Sender npi of message 2/2 should be 1 ==> expected: <1> but was: <8>'],
				 [klass: AssertionFailedError, message: 'Receiver number of message 2/2 should be 1111111111 ==> expected: <1111111111> but was: <0605040302>'],
				 [klass: AssertionFailedError, message: 'Receiver ton of message 2/2 should be 0 ==> expected: <0> but was: <5>'],
				 [klass: AssertionFailedError, message: 'Receiver npi of message 2/2 should be 10 ==> expected: <10> but was: <3>'],
				 [klass: AssertionFailedError, message: 'Message 2/2 not consistent with expected ==> expected: <bar> but was: <part2>']
				]
			"missing part"			| ["part1", "part2", "part3"]	| "0102030405"	| INTERNATIONAL	| NATIONAL	| "0605040302"		| ALPHANUMERIC	| DATA			|| [
				 [klass: AssertionFailedError, message: 'should have received exactly 3 message(s) ==> expected: <3> but was: <2>'],
				 [klass: AssertionFailedError, message: 'Sender number of message 3/3 should be 0102030405 ==> expected: <0102030405> but was: <null>'],
				 [klass: AssertionFailedError, message: 'Sender ton of message 3/3 should be 1 ==> expected: <1> but was: <null>'],
				 [klass: AssertionFailedError, message: 'Sender npi of message 3/3 should be 8 ==> expected: <8> but was: <null>'],
				 [klass: AssertionFailedError, message: 'Receiver number of message 3/3 should be 0605040302 ==> expected: <0605040302> but was: <null>'],
				 [klass: AssertionFailedError, message: 'Receiver ton of message 3/3 should be 5 ==> expected: <5> but was: <null>'],
				 [klass: AssertionFailedError, message: 'Receiver npi of message 3/3 should be 3 ==> expected: <3> but was: <null>'],
				 [klass: AssertionFailedError, message: 'Message 3/3 not consistent with expected ==> expected: <part3> but was: <null>']
				]
			"too many parts"		| ["part1"]						| "0102030405"	| INTERNATIONAL	| NATIONAL	| "0605040302"		| ALPHANUMERIC	| DATA			|| [
				 [klass: AssertionFailedError, message: 'should have received exactly 1 message(s) ==> expected: <1> but was: <2>'],
				]
	}


	private List collectFailures(Closure<?>... cls) {
		def failures = []
		for (Closure cl : cls) {
			try {
				cl()
			} catch(MultipleAssertionError e) {
				failures += this.failures(e)
			}
		}
		return failures
	}
	
	private List failures(MultipleAssertionError e) {
		def assertions = []
		for (Throwable t : e.getFailures()) {
			assertions += [klass: t.getClass(), message: t.getMessage().replaceAll("\\s+", " ")];
		}
		return assertions
	}
}
