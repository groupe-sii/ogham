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
import static org.hamcrest.Matchers.hasSize
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
import fr.sii.ogham.testing.extension.junit.sms.SmppServerRule
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
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getSourceAddr() >> "0102030405"
					getSourceAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getSourceAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getSourceAddr() >> "0106050403"
					getSourceAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getSourceAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
		
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
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getDestAddress() >> "0102030405"
					getDestAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getDestAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getDestAddress() >> "0106050403"
					getDestAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getDestAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
		
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
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms1")
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms2")
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
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
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms1")
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms2")
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
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
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms1")
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms2")
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
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
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<115b>, <109b>, <115b>, <49b>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <49b>]'], 
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<115b>, <109b>, <115b>, <49b>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <49b>]'], 
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<115b>, <109b>, <115b>, <50b>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <50b>]'], 
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<115b>, <109b>, <115b>, <50b>]'], 
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was null'], 
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <50b>]']
					]
	}

	def "udhi | shortMessage(#matcher) & shortMessage().header(#headerMatcher) & shortMessage().payload(#payloadMatcher) #desc"() {
		given:
			def header = [0x05, 0, 0, 0, 0, 0]
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> ((header + (CHARSET_GSM8.encode("sms1") as List)) as byte[])
					isUdhi() >> true
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> ((header + (CHARSET_GSM8.encode("sms2") as List)) as byte[])
					isUdhi() >> true
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
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
			"should pass"			| arrayWithSize(6 + 4)	| arrayWithSize(6)	| arrayWithSize(4)	|| []
			"should detect all"		| arrayWithSize(2)		| arrayWithSize(1)	| arrayWithSize(1)	|| [
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>, <115b>, <109b>, <115b>, <49b>]'],
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>]'],
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <49b>]'],
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>, <115b>, <109b>, <115b>, <49b>]'],
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>]'],
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <49b>]'],
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>, <115b>, <109b>, <115b>, <50b>]'],
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>]'],
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <50b>]'],
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>, <115b>, <109b>, <115b>, <50b>]'],
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was [<5b>, <0b>, <0b>, <0b>, <0b>, <0b>]'],
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <50b>]']
					]
	}

	
	def "encoding(#encodingMatcher) & alphabet(#alphabetMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
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
					 [klass: AssertionError, message: 'encoding of raw request of message 0 Expected: is <0b> but: was <4b>'], 
					 [klass: AssertionError, message: 'alphabet of raw request of message 0 Expected: is <ALPHA_LATIN1> but: was <ALPHA_8_BIT>'], 
					 [klass: AssertionError, message: 'encoding of raw request of message 1 Expected: is <0b> but: was <4b>'], 
					 [klass: AssertionError, message: 'alphabet of raw request of message 1 Expected: is <ALPHA_LATIN1> but: was <ALPHA_8_BIT>']
					]
	}

	def "optionalParameter(#tag).parameter(#paramMatcher) & optionalParameter(#tag).value(#valueMatcher) & optionalParameter(#tag).length(#lengthMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getOptionalParameter(Tag.NUMBER_OF_MESSAGES.code()) >> new Number_of_messages((byte) 2)
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getOptionalParameter(Tag.NUMBER_OF_MESSAGES.code()) >> new Number_of_messages((byte) 3)
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
			
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
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' value of raw request of message 0 Expected: [a value less than <1b>] but: was [<2b>]'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' length of raw request of message 0 Expected: is <2> but: was <1>'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' of raw request of message 1 Expected: hasProperty("tag", is <0>) but: was <[772|1|03]>'], 
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' value of raw request of message 1 Expected: [a value less than <1b>] but: was [<3b>]'], 
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
	
	def "every() | from(#matcher) & from().number(#numberMatcher) & from().numeringPlanIndicator(#npiMatcher) & from().typeOfNumber(#tonMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getSourceAddr() >> "0102030405"
					getSourceAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getSourceAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getSourceAddr() >> "0106050403"
					getSourceAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getSourceAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
		
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
				 [klass: AssertionError, message: 'sender of message 1 Expected: null but: was <PhoneNumberInfo [address=0106050403, npi=8, ton=1]>'],
				 [klass: AssertionError, message: 'number of sender of message 0 Expected: is "0" but: was "0102030405"'],
				 [klass: AssertionError, message: 'number of sender of message 1 Expected: is "0" but: was "0106050403"'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of sender of message 0 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of sender of message 1 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of sender of message 0 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of sender of message 1 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				]
	}
	
	def "every() | to(#matcher) & to().number(#numberMatcher) & to().numeringPlanIndicator(#npiMatcher) & to().typeOfNumber(#tonMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getDestAddress() >> "0102030405"
					getDestAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getDestAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getDestAddress() >> "0106050403"
					getDestAddrNpi() >> org.jsmpp.bean.NumberingPlanIndicator.NATIONAL.value()
					getDestAddrTon() >> org.jsmpp.bean.TypeOfNumber.INTERNATIONAL.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
		
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
				 [klass: AssertionError, message: 'recipient of message 1 Expected: null but: was <PhoneNumberInfo [address=0106050403, npi=8, ton=1]>'],
				 [klass: AssertionError, message: 'number of recipient of message 0 Expected: is "0" but: was "0102030405"'],
				 [klass: AssertionError, message: 'number of recipient of message 1 Expected: is "0" but: was "0106050403"'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of recipient of message 0 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'NumberPlanIndicator of recipient of message 1 Expected: is <DATA> but: was <NATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of recipient of message 0 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				 [klass: AssertionError, message: 'TypeOfNumber of recipient of message 1 Expected: is <ALPHANUMERIC> but: was <INTERNATIONAL>'],
				]
	}

	def "every() | content(#matcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms1")
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms2")
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
			
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
					 [klass: AssertionError, message: 'content of message 1 Expected: is "foo" but: was "sms2"']
					]
	}
	
	def "every() | content(#charset, #matcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms1")
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms2")
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
					 [klass: AssertionError, message: 'content of message 1 Expected: a string starting with "sms" but: was "sZMΓ"']
					]
	}

	def "every() | shortMessage(#matcher) & shortMessage().header(#headerMatcher) & shortMessage().payload(#payloadMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms1")
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getShortMessage() >> CHARSET_GSM8.encode("sms2")
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
			
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
					 [klass: AssertionError, message: 'shortMessage of raw request of message 0 Expected: an array with size <2> but: was [<115b>, <109b>, <115b>, <49b>]'],
					 [klass: AssertionError, message: 'shortMessage of raw request of message 1 Expected: an array with size <2> but: was [<115b>, <109b>, <115b>, <50b>]'],
					 [klass: AssertionError, message: 'header of raw request of message 0 Expected: an array with size <1> but: was null'],
					 [klass: AssertionError, message: 'header of raw request of message 1 Expected: an array with size <1> but: was null'],
					 [klass: AssertionError, message: 'payload of raw request of message 0 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <49b>]'],
					 [klass: AssertionError, message: 'payload of raw request of message 1 Expected: an array with size <1> but: was [<115b>, <109b>, <115b>, <50b>]']
					]
	}

	
	def "every() | encoding(#encodingMatcher) & alphabet(#alphabetMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getDataCoding() >> ALPHA_8_BIT.value()
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
					 [klass: AssertionError, message: 'encoding of raw request of message 0 Expected: is <0b> but: was <4b>'],
					 [klass: AssertionError, message: 'encoding of raw request of message 1 Expected: is <0b> but: was <4b>'],
					 [klass: AssertionError, message: 'alphabet of raw request of message 0 Expected: is <ALPHA_LATIN1> but: was <ALPHA_8_BIT>'],
					 [klass: AssertionError, message: 'alphabet of raw request of message 1 Expected: is <ALPHA_LATIN1> but: was <ALPHA_8_BIT>']
					]
	}

	def "every() | optionalParameter(#tag).parameter(#paramMatcher) & optionalParameter(#tag).value(#valueMatcher) & optionalParameter(#tag).length(#lengthMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				org.jsmpp.bean.SubmitSm sms1 = Mock {
					getOptionalParameter(Tag.NUMBER_OF_MESSAGES.code()) >> new Number_of_messages((byte) 2)
				}
				org.jsmpp.bean.SubmitSm sms2 = Mock {
					getOptionalParameter(Tag.NUMBER_OF_MESSAGES.code()) >> new Number_of_messages((byte) 3)
				}
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
			
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg)
						.receivedMessages()
							.every()
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
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' of raw request of message 1 Expected: hasProperty("tag", is <0>) but: was <[772|1|03]>'],
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' value of raw request of message 0 Expected: [a value less than <1b>] but: was [<2b>]'],
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' value of raw request of message 1 Expected: [a value less than <1b>] but: was [<3b>]'],
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' length of raw request of message 0 Expected: is <2> but: was <1>'],
					 [klass: AssertionError, message: 'optional parameter \'number_of_messages\' length of raw request of message 1 Expected: is <2> but: was <1>']
					]
			"missing tag"			| CALLBACK_NUM			| hasProperty("tag", is(0))								| lessThan(1)		| is(2)				|| [
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) of raw request of message 0 Expected: hasProperty("tag", is <0>) but: was <[null|null|]>'],
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) of raw request of message 1 Expected: hasProperty("tag", is <0>) but: was <[null|null|]>'],
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) value of raw request of message 0 Expected: a value less than <1> but: was null'],
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) value of raw request of message 1 Expected: a value less than <1> but: was null'],
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) length of raw request of message 0 Expected: is <2> but: was null'],
					 [klass: AssertionError, message: 'optional parameter \'callback_num\' (/!\\ not found) length of raw request of message 1 Expected: is <2> but: was null']
					]
	}
	
	def "assertions on message #index but only #messages.size() messages received"() {
		given:
			SmppServerRule smpp = Mock {
				getReceivedMessages() >> messages
			}
		
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg).receivedMessage(index) },
					(Consumer) { reg -> assertThat(smpp, reg).receivedMessages().message(index) },
				)
			}

		then:
			failures == expected
			
		where:
			messages				| index			|| expected
			[]						| 0				||  [
														 [klass: AssertionError, message: "Assertions on message 0 can't be executed because 0 messages were received"],
														 [klass: AssertionError, message: "Assertions on message 0 can't be executed because 0 messages were received"],
														]
			[]						| 1				||  [
														 [klass: AssertionError, message: "Assertions on message 1 can't be executed because 0 messages were received"],
														 [klass: AssertionError, message: "Assertions on message 1 can't be executed because 0 messages were received"],
														]
			[Mock(SubmitSm)]		| 1				||  [
														 [klass: AssertionError, message: "Assertions on message 1 can't be executed because 1 messages were received"],
														 [klass: AssertionError, message: "Assertions on message 1 can't be executed because 1 messages were received"],
														]
			[Mock(SubmitSm)]		| 2				||  [
														 [klass: AssertionError, message: "Assertions on message 2 can't be executed because 1 messages were received"],
														 [klass: AssertionError, message: "Assertions on message 2 can't be executed because 1 messages were received"],
														]
	}

	def "#messages.size() messages received & receivedMessages(#matcher) & count(#countMatcher) #desc"() {
		given:
			SmppServerRule smpp = Mock {
				getReceivedMessages() >> messages
			}
		
		when:
			def failures = collectFailures {
				assertAll(
					(Consumer) { reg -> assertThat(smpp, reg).receivedMessages(matcher) },
					(Consumer) { reg -> assertThat(smpp, reg).receivedMessages().count(countMatcher) },
				)
			}
			
		then:
			failures == expected
			
		where:
			desc 					| messages	| matcher		| countMatcher		|| expected
			"should pass"			| []		| hasSize(0)	| is(0)				||  []
			"should be detected"	| []		| hasSize(1)	| is(1)				||  [
																						 [klass: AssertionError, message: "Received messages Expected: a collection with size <1> but: collection size was <0>"],
																						 [klass: AssertionError, message: "Received messages count Expected: is <1> but: was <0>"],
																						]
	}

	def "fail immediately | from(#matcher) & from().number(#numberMatcher) #desc"() {
		given:
			org.jsmpp.bean.SubmitSm sms1 = Mock {
				getSourceAddr() >> "0102030405"
			}
			org.jsmpp.bean.SubmitSm sms2 = Mock {
				getSourceAddr() >> "0106050403"
			}
			SmppServerRule smpp = Mock {
				getReceivedMessages() >> [new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)]
			}
		
		when:
			def failures = collectFailures({
					assertThat(smpp)
						.receivedMessage(0)
							.from(matcher)
							.from()
								.number(numberMatcher)
								.and()
							.and()
						.receivedMessage(1)
							.from(matcher)
							.from()
								.number(numberMatcher)
			}, {
				assertThat([new SubmitSmAdapter(sms1), new SubmitSmAdapter(sms2)])
					.message(0)
						.from(matcher)
						.from()
							.number(numberMatcher)
							.and()
						.and()
					.message(1)
						.from(matcher)
						.from()
							.number(numberMatcher)

			})
		
		then:
			failures == expected
			
		where:
			desc					| matcher			| numberMatcher			| npiMatcher		| tonMatcher		|| expected
			"should pass"			| notNullValue()	| startsWith("01")		| is(NATIONAL)		| is(INTERNATIONAL)	|| []
			"should detect all"		| nullValue()		| is("0")				| is(DATA)			| is(ALPHANUMERIC)	|| [
				 [klass: AssertionError, message: 'sender of message 0 Expected: null but: was <PhoneNumberInfo [address=0102030405, npi=0, ton=0]>'],
				 [klass: AssertionError, message: 'sender of message 0 Expected: null but: was <PhoneNumberInfo [address=0102030405, npi=0, ton=0]>'],
				]
	}

	
	private List collectFailures(Closure<?>... cls) {
		def assertions = []
		for (Closure cl : cls) {
			assertions += collect(cl)
		}
		return assertions
	}
	
	private List collect(Closure cl) {
		try {
			cl()
			return []
		} catch(MultipleAssertionError e) {
			return failures(e)
		} catch(Throwable e) {
			return [[klass: e.getClass(), message: e.getMessage().replaceAll("\\s+", " ")]]
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
