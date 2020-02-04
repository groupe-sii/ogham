package oghamtesting.it.assertion

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertAll
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat
import static fr.sii.ogham.testing.sms.simulator.bean.Alphabet.ALPHA_8_BIT
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.DATA
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.NATIONAL
import static fr.sii.ogham.testing.sms.simulator.bean.Tag.CALLBACK_NUM
import static fr.sii.ogham.testing.sms.simulator.bean.Tag.NUMBER_OF_MESSAGES
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.ALPHANUMERIC
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.INTERNATIONAL
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

import org.jsmpp.bean.OptionalParameter
import org.jsmpp.bean.OptionalParameter.Number_of_messages
import org.jsmpp.bean.OptionalParameter.Tag

import com.cloudhopper.commons.charset.CharsetUtil

import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.extension.junit.SmppServerRule
import fr.sii.ogham.testing.sms.simulator.bean.Address
import fr.sii.ogham.testing.sms.simulator.bean.Alphabet
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber
import fr.sii.ogham.testing.sms.simulator.decode.CloudhopperCharsetAdapter
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class FluentSmsAssertionsSpec extends Specification {
	def setupSpec() {
		System.setProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "false");
	}
	
	def cleanupSpec() {
		System.clearProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure");
	}

	def "from(#matcher) & from().number(#numberMatcher) & from().numeringPlanIndicator(#npiMatcher) & from().typeOfNumber(#tonMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getSourceAddr() >> "0102030405"
			sms1.getSourceAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
			sms1.getSourceAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()

			sms2.getSourceAddr() >> "0106050403"
			sms2.getSourceAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
			sms2.getSourceAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
		
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(0)
							.from(matcher)
							.from()
								.number(numberMatcher)
								.numberingPlanIndicator(npiMatcher)
								.typeOfNumber(tonMatcher)
								.and()
							.and()
						.receivedMessages()
							.message(0)
								.from(matcher)
								.from()
									.number(numberMatcher)
									.numberingPlanIndicator(npiMatcher)
									.typeOfNumber(tonMatcher) },
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(1)
							.from(matcher)
							.from()
								.number(numberMatcher)
								.numberingPlanIndicator(npiMatcher)
								.typeOfNumber(tonMatcher)
								.and()
							.and()
						.receivedMessages()
							.message(1)
								.from(matcher)
								.from()
								.number(numberMatcher)
								.numberingPlanIndicator(npiMatcher)
								.typeOfNumber(tonMatcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc					| matcher			| numberMatcher			| npiMatcher		| tonMatcher		|| expected
			"should pass"			| notNullValue()	| startsWith("01")		| is(NATIONAL)		| is(INTERNATIONAL)	|| []
			"should detect all"		| nullValue()		| is("0")				| is(DATA)			| is(ALPHANUMERIC)	|| [
				 [klass: AssertionError, message: 'sender of message 0 Expected: null but: was <PhoneNumberInfo [address=0102030405, npi=8, ton=1]>'], 
				 [klass: AssertionError, message: 'number of sender of message 0 Expected: is "0" but: was "0102030405"'], 
				 [klass: AssertionError, message: 'NumberPlanIndicator of sender of message 0 Expected: is <DATA> but: was <NATIONAL>'], 
				 [klass: AssertionError, message: 'TypeOfNumber of sender of message 0 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'], 
				 [klass: AssertionError, message: 'sender of message 0 Expected: null but: was <PhoneNumberInfo [address=0102030405, npi=8, ton=1]>'], 
				 [klass: AssertionError, message: 'number of sender of message 0 Expected: is "0" but: was "0102030405"'], 
				 [klass: AssertionError, message: 'NumberPlanIndicator of sender of message 0 Expected: is <DATA> but: was <NATIONAL>'], 
				 [klass: AssertionError, message: 'TypeOfNumber of sender of message 0 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'], 
				 [klass: AssertionError, message: 'sender of message 1 Expected: null but: was <PhoneNumberInfo [address=0106050403, npi=8, ton=1]>'], 
				 [klass: AssertionError, message: 'number of sender of message 1 Expected: is "0" but: was "0106050403"'], 
				 [klass: AssertionError, message: 'NumberPlanIndicator of sender of message 1 Expected: is <DATA> but: was <NATIONAL>'], 
				 [klass: AssertionError, message: 'TypeOfNumber of sender of message 1 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'], 
				 [klass: AssertionError, message: 'sender of message 1 Expected: null but: was <PhoneNumberInfo [address=0106050403, npi=8, ton=1]>'], 
				 [klass: AssertionError, message: 'number of sender of message 1 Expected: is "0" but: was "0106050403"'], 
				 [klass: AssertionError, message: 'NumberPlanIndicator of sender of message 1 Expected: is <DATA> but: was <NATIONAL>'], 
				 [klass: AssertionError, message: 'TypeOfNumber of sender of message 1 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>']
				]
	}
	
	def "to(#matcher) & to().number(#numberMatcher) & to().numeringPlanIndicator(#npiMatcher) & to().typeOfNumber(#tonMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getDestAddress() >> "0102030405"
			sms1.getDestAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
			sms1.getDestAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()

			sms2.getDestAddress() >> "0106050403"
			sms2.getDestAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
			sms2.getDestAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
		
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(0)
							.to(matcher)
							.to()
								.number(numberMatcher)
								.numberingPlanIndicator(npiMatcher)
								.typeOfNumber(tonMatcher)
								.and()
							.and()
						.receivedMessages()
							.message(0)
								.to(matcher)
								.to()
									.number(numberMatcher)
									.numberingPlanIndicator(npiMatcher)
									.typeOfNumber(tonMatcher)
									.and()
								.and()
							.and()
						.receivedMessage(1)
							.to(matcher)
							.to()
								.number(numberMatcher)
								.numberingPlanIndicator(npiMatcher)
								.typeOfNumber(tonMatcher)
								.and()
							.and()
						.receivedMessages()
							.message(1)
								.to(matcher)
								.to()
									.number(numberMatcher)
									.numberingPlanIndicator(npiMatcher)
									.typeOfNumber(tonMatcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc					| matcher			| numberMatcher			| npiMatcher		| tonMatcher		|| expected
			"should pass"			| notNullValue()	| startsWith("01")		| is(NATIONAL)		| is(INTERNATIONAL)	|| []
			"should detect all"		| nullValue()		| is("0")				| is(DATA)			| is(ALPHANUMERIC)	|| [
				 [klass: AssertionError, message: 'recipient of message 0 Expected: null but: was <PhoneNumberInfo [address=0102030405, npi=8, ton=1]>'],
				 [klass: AssertionError, message: 'number of recipient of message 0 Expected: is "0" but: was "0102030405"'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of recipient of message 0 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of recipient of message 0 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				 [klass: AssertionError, message: 'recipient of message 0 Expected: null but: was <PhoneNumberInfo [address=0102030405, npi=8, ton=1]>'],
				 [klass: AssertionError, message: 'number of recipient of message 0 Expected: is "0" but: was "0102030405"'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of recipient of message 0 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of recipient of message 0 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				 [klass: AssertionError, message: 'recipient of message 1 Expected: null but: was <PhoneNumberInfo [address=0106050403, npi=8, ton=1]>'],
				 [klass: AssertionError, message: 'number of recipient of message 1 Expected: is "0" but: was "0106050403"'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of recipient of message 1 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of recipient of message 1 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				 [klass: AssertionError, message: 'recipient of message 1 Expected: null but: was <PhoneNumberInfo [address=0106050403, npi=8, ton=1]>'],
				 [klass: AssertionError, message: 'number of recipient of message 1 Expected: is "0" but: was "0106050403"'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of recipient of message 1 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of recipient of message 1 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>']
				]
	}

	def "content(#matcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getShortMessage() >> CHARSET_GSM8.encode("sms1")
			sms1.getDataCoding() >> ALPHA_8_BIT.value()
			sms2.getShortMessage() >> CHARSET_GSM8.encode("sms2")
			sms2.getDataCoding() >> ALPHA_8_BIT.value()
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(0)
							.content(matcher)
							.and()
						.receivedMessages()
							.message(0)
								.content(matcher)
								.and()
							.and()
						.receivedMessage(1)
							.content(matcher)
							.and()
						.receivedMessages()
							.message(1)
								.content(matcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc					| matcher				|| expected
			"should pass"			| startsWith("sms")		|| []
			"should detect all"		| is("foo")				|| [
					 [klass: AssertionError, message: 'content of message 0 Expected: is "foo" but: was "sms1"'], 
					 [klass: AssertionError, message: 'content of message 0 Expected: is "foo" but: was "sms1"'], 
					 [klass: AssertionError, message: 'content of message 1 Expected: is "foo" but: was "sms2"'], 
					 [klass: AssertionError, message: 'content of message 1 Expected: is "foo" but: was "sms2"']
					]
	}
	
	def "content(#charset, #matcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getShortMessage() >> CHARSET_GSM8.encode("sms1")
			sms2.getShortMessage() >> CHARSET_GSM8.encode("sms2")
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(0)
							.content(new CloudhopperCharsetAdapter(charset), matcher)
							.and()
						.receivedMessages()
							.message(0)
								.content(new CloudhopperCharsetAdapter(charset), matcher)
								.and()
							.and()
						.receivedMessage(1)
							.content(new CloudhopperCharsetAdapter(charset), matcher)
							.and()
						.receivedMessages()
							.message(1)
								.content(new CloudhopperCharsetAdapter(charset), matcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc							| charset		| matcher				|| expected
			"should pass"					| CHARSET_GSM8	| startsWith("sms")		|| []
			"should detect wrong encoding"	| CHARSET_GSM7	| startsWith("sms")		|| [
					 [klass: AssertionError, message: 'content of message 0 Expected: a string starting with "sms" but: was "sZMØ"'],
					 [klass: AssertionError, message: 'content of message 0 Expected: a string starting with "sms" but: was "sZMØ"'],
					 [klass: AssertionError, message: 'content of message 1 Expected: a string starting with "sms" but: was "sZMΓ"'],
					 [klass: AssertionError, message: 'content of message 1 Expected: a string starting with "sms" but: was "sZMΓ"']
					]
	}

	def "shortMessage(#matcher) & shortMessage().header(#headerMatcher) & shortMessage().payload(#payloadMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getShortMessage() >> CHARSET_GSM8.encode("sms1")
			sms2.getShortMessage() >> CHARSET_GSM8.encode("sms2")
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(0)
							.rawRequest()
								.shortMessage(matcher)
								.shortMessage()
									.header(headerMatcher)
									.payload(payloadMatcher) },
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.message(0)
								.rawRequest()
									.shortMessage(matcher)
									.shortMessage()
										.header(headerMatcher)
										.payload(payloadMatcher) },
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessage(1)
							.rawRequest()
								.shortMessage(matcher)
								.shortMessage()
									.header(headerMatcher)
									.payload(payloadMatcher) },
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.message(1)
								.rawRequest()
									.shortMessage(matcher)
									.shortMessage()
										.header(headerMatcher)
										.payload(payloadMatcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc					| matcher				| headerMatcher		| payloadMatcher	|| expected
			"should pass"			| arrayWithSize(4)		| nullValue()		| arrayWithSize(4)	|| []
			"should detect all"		| arrayWithSize(2)		| arrayWithSize(1)	| arrayWithSize(1)	|| [
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<115>, <109>, <115>, <49>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115>, <109>, <115>, <49>]'], 
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<115>, <109>, <115>, <49>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115>, <109>, <115>, <49>]'], 
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<115>, <109>, <115>, <50>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115>, <109>, <115>, <50>]'], 
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<115>, <109>, <115>, <50>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115>, <109>, <115>, <50>]']
					]
	}

	
	def "encoding(#encodingMatcher) & alphabet(#alphabetMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getDataCoding() >> ALPHA_8_BIT.value()
			sms2.getDataCoding() >> ALPHA_8_BIT.value()
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.message(0)
								.rawRequest()
									.encoding(encodingMatcher)
									.alphabet(alphabetMatcher) },
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.message(1)
								.rawRequest()
									.encoding(encodingMatcher)
									.alphabet(alphabetMatcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc					| encodingMatcher						| alphabetMatcher			|| expected
			"should pass"			| is(Alphabet.ALPHA_8_BIT.value())		| is(Alphabet.ALPHA_8_BIT)	|| []
			"should detect all"		| is(Alphabet.ALPHA_DEFAULT.value())	| is(Alphabet.ALPHA_LATIN1)	|| [
					 [klass: AssertionError, message: 'encoding of raw request of message 0 Expected: is <0> but: was <4>'], 
					 [klass: AssertionError, message: 'alphabet of raw request of message 0 Expected: is <ALPHA_LATIN1> but: was <ALPHA_8_BIT>'], 
					 [klass: AssertionError, message: 'encoding of raw request of message 1 Expected: is <0> but: was <4>'], 
					 [klass: AssertionError, message: 'alphabet of raw request of message 1 Expected: is <ALPHA_LATIN1> but: was <ALPHA_8_BIT>']
					]
	}

	def "optionalParameter(#tag).parameter(#paramMatcher) & optionalParameter(#tag).value(#valueMatcher) & optionalParameter(#tag).length(#lengthMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock()
			org.jsmpp.bean.SubmitSm sms1 = Mock()
			org.jsmpp.bean.SubmitSm sms2 = Mock()
			smpp.getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			
			sms1.getOptionalParameter(Tag.NUMBER_OF_MESSAGES.code()) >> new Number_of_messages((byte) 2)
			sms2.getOptionalParameter(Tag.NUMBER_OF_MESSAGES.code()) >> new Number_of_messages((byte) 3)
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.message(0)
								.rawRequest()
									.optionalParameter(tag)
										.parameter(paramMatcher)
										.value(valueMatcher)
										.length(lengthMatcher) },
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.message(1)
								.rawRequest()
									.optionalParameter(tag)
										.parameter(paramMatcher)
										.value(valueMatcher)
										.length(lengthMatcher) },
				)
			}
		
		then:
			failures == expected
			
		where:
			desc					| tag					| paramMatcher											| valueMatcher					| lengthMatcher		|| expected
			"should pass"			| NUMBER_OF_MESSAGES	| hasProperty("tag", is(NUMBER_OF_MESSAGES.getCode()))	| array(greaterThan((Byte)1))	| is(1)				|| []
			"should detect all"		| NUMBER_OF_MESSAGES	| hasProperty("tag", is(0))								| array(lessThan((Byte) 1))		| is(2)				|| [
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' of raw request of message 0 Expected: hasProperty("tag", is <0>) but: was <[772|1|02]>'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' value of raw request of message 0 Expected: [a value less than <1>] but: was [<2>]'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' length of raw request of message 0 Expected: is <2> but: was <1>'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' of raw request of message 1 Expected: hasProperty("tag", is <0>) but: was <[772|1|03]>'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' value of raw request of message 1 Expected: [a value less than <1>] but: was [<3>]'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' length of raw request of message 1 Expected: is <2> but: was <1>']
					]
			"missing tag"			| CALLBACK_NUM			| hasProperty("tag", is(0))								| lessThan(1)		| is(2)				|| [
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) of raw request of message 0 Expected: hasProperty("tag", is <0>) but: was <[null|null|]>'], 
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) value of raw request of message 0 Expected: a value less than <1> but: was null'], 
				 	 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) length of raw request of message 0 Expected: is <2> but: was null'], 
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) of raw request of message 1 Expected: hasProperty("tag", is <0>) but: was <[null|null|]>'], 
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) value of raw request of message 1 Expected: a value less than <1> but: was null'], 
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) length of raw request of message 1 Expected: is <2> but: was null']
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
