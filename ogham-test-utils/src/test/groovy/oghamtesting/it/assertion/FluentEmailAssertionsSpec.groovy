package oghamtesting.it.assertion

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertAll
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat
import static javax.mail.Message.RecipientType.CC
import static javax.mail.Message.RecipientType.TO
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.arrayWithSize
import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.everyItem
import static org.hamcrest.Matchers.hasItems
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static org.hamcrest.Matchers.startsWith

import java.util.function.Consumer

import javax.mail.BodyPart
import javax.mail.Multipart
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

import com.icegreen.greenmail.junit.GreenMailRule

import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class FluentEmailAssertionsSpec extends Specification {
	def setupSpec() {
		System.setProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "false");
	}
	
	def cleanupSpec() {
		System.clearProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure");
	}
	
	def "message(0).subject(#matcher1) & message(1).subject(#matcher2) with #subject1 & #subject2 #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			message1.getSubject() >> subject1
			MimeMessage message2 = Mock()
			message2.getSubject() >> subject2
			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessage(0).subject(matcher1).and().receivedMessage(1).subject(matcher2) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().message(0).subject(matcher1).and().message(1).subject(matcher2) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject1		| subject2			| matcher1			| matcher2			|| expected
			"should pass"			| "subject1"	| "subject2"		| is("subject1")	| is("subject2")	||  []
			"should be detected"	| "subject1"	| "subject2"		| is("subject1")	| nullValue()		||  [
					 [klass: AssertionError, message: 'subject of message 1 Expected: null but: was "subject2"'],
					 [klass: AssertionError, message: 'subject of message 1 Expected: null but: was "subject2"'],
					]
			"should be detected"	| "subject1"	| "subject2"		| nullValue()		| is("subject2")	||  [
					 [klass: AssertionError, message: 'subject of message 0 Expected: null but: was "subject1"'],
					 [klass: AssertionError, message: 'subject of message 0 Expected: null but: was "subject1"'],
					]
			"should be detected"	| "subject1"	| "subject2"		| nullValue()		| nullValue()		||  [
					 [klass: AssertionError, message: 'subject of message 0 Expected: null but: was "subject1"'],
					 [klass: AssertionError, message: 'subject of message 1 Expected: null but: was "subject2"'],
					 [klass: AssertionError, message: 'subject of message 0 Expected: null but: was "subject1"'],
					 [klass: AssertionError, message: 'subject of message 1 Expected: null but: was "subject2"'],
					]
	}
	
	def "every().subject(#matcher) with #subject1 & #subject2 #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			message1.getSubject() >> subject1
			MimeMessage message2 = Mock()
			message2.getSubject() >> subject2
			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().every().subject(matcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject1		| subject2		| matcher		|| expected
			"should pass"			| "foo"			| "foo"			| is("foo")		||  []
			"should be detected"	| "foo"			| "bar"			| is("foo")		||  [
					 [klass: AssertionError, message: 'subject of message 1 Expected: is "foo" but: was "bar"'],
					]
			"should be detected"	| "bar"			| "foo"			| is("foo")		||  [
					 [klass: AssertionError, message: 'subject of message 0 Expected: is "foo" but: was "bar"'],
					]
			"should be detected"	| "bar"			| "baz"			| is("foo")		||  [
					 [klass: AssertionError, message: 'subject of message 0 Expected: is "foo" but: was "bar"'],
					 [klass: AssertionError, message: 'subject of message 1 Expected: is "foo" but: was "baz"'],
					]
	}
	
	def "from().address(#addressMacher) & from().personal(#personalMacher) & from().textual(#textualMacher) & from().type(#typeMacher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			InternetAddress from1 = Mock()
			message1.getFrom() >> ([from1] as InternetAddress[])
			from1.getAddress() >> "address1"
			from1.getPersonal() >> "personal1"
			from1.getType() >> "type1"
			from1.toString() >> "personal1 <address1>"
			
			MimeMessage message2 = Mock()
			InternetAddress from2 = Mock()
			message2.getFrom() >> ([from2] as InternetAddress[])
			from2.getAddress() >> "address2"
			from2.getPersonal() >> "personal2"
			from2.getType() >> "type2"
			from2.toString() >> "personal2 <address2>"
			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg)
						.receivedMessages()
							.message(0)
								.from()
									.address(addressMatcher)
									.personal(personalMatcher)
									.textual(textualMatcher)
									.type(typeMatcher)
									.and()
								.and()
							.message(1)
								.from()
									.address(addressMatcher)
									.personal(personalMatcher)
									.textual(textualMatcher)
									.type(typeMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| addressMatcher					| personalMatcher					| textualMatcher														| typeMatcher					|| expected
			"should pass"			| everyItem(startsWith("address"))	| everyItem(startsWith("personal"))	| everyItem(allOf(startsWith("personal"), containsString("address")))	| everyItem(startsWith("type"))	||  []
			"should detect all"		| everyItem(is("address"))			| everyItem(is("personal"))			| everyItem(is("personal <address>"))									| everyItem(is("type"))			||  [
						 [klass: AssertionError, message: "email addresses of 'from' field of message 0 Expected: every item is is \"address\" but: was <[address1]>"], 
						 [klass: AssertionError, message: "personal of 'from' field of message 0 Expected: every item is is \"personal\" but: was <[personal1]>"], 
						 [klass: AssertionError, message: "textual addresses of 'from' field of message 0 Expected: every item is is \"personal <address>\" but: was <[personal1 <address1>]>"], 
						 [klass: AssertionError, message: "address types of 'from' field of message 0 Expected: every item is is \"type\" but: was <[type1]>"], 
						 [klass: AssertionError, message: "email addresses of 'from' field of message 1 Expected: every item is is \"address\" but: was <[address2]>"], 
						 [klass: AssertionError, message: "personal of 'from' field of message 1 Expected: every item is is \"personal\" but: was <[personal2]>"], 
						 [klass: AssertionError, message: "textual addresses of 'from' field of message 1 Expected: every item is is \"personal <address>\" but: was <[personal2 <address2>]>"], 
						 [klass: AssertionError, message: "address types of 'from' field of message 1 Expected: every item is is \"type\" but: was <[type2]>"]
						]
	}
	
	
	def "to().address(#addressMacher) & to().personal(#personalMacher) & to().textual(#textualMacher) & to().type(#typeMacher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			InternetAddress to1 = Mock()
			message1.getRecipients(TO) >> ([to1] as InternetAddress[])
			to1.getAddress() >> "address1"
			to1.getPersonal() >> "personal1"
			to1.getType() >> "type1"
			to1.toString() >> "personal1 <address1>"
			
			MimeMessage message2 = Mock()
			InternetAddress to2 = Mock()
			message2.getRecipients(TO) >> ([to2] as InternetAddress[])
			to2.getAddress() >> "address2"
			to2.getPersonal() >> "personal2"
			to2.getType() >> "type2"
			to2.toString() >> "personal2 <address2>"
			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg)
						.receivedMessages()
							.message(0)
								.to()
									.address(addressMatcher)
									.personal(personalMatcher)
									.textual(textualMatcher)
									.type(typeMatcher)
									.and()
								.and()
							.message(1)
								.to()
									.address(addressMatcher)
									.personal(personalMatcher)
									.textual(textualMatcher)
									.type(typeMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| addressMatcher					| personalMatcher					| textualMatcher														| typeMatcher					|| expected
			"should pass"			| everyItem(startsWith("address"))	| everyItem(startsWith("personal"))	| everyItem(allOf(startsWith("personal"), containsString("address")))	| everyItem(startsWith("type"))	||  []
			"should detect all"		| everyItem(is("address"))			| everyItem(is("personal"))			| everyItem(is("personal <address>"))									| everyItem(is("type"))			||  [
					 [klass: AssertionError, message: "email addresses of 'to' field of message 0 Expected: every item is is \"address\" but: was <[address1]>"], 
					 [klass: AssertionError, message: "personal of 'to' field of message 0 Expected: every item is is \"personal\" but: was <[personal1]>"], 
					 [klass: AssertionError, message: "textual addresses of 'to' field of message 0 Expected: every item is is \"personal <address>\" but: was <[personal1 <address1>]>"], 
					 [klass: AssertionError, message: "address types of 'to' field of message 0 Expected: every item is is \"type\" but: was <[type1]>"], 
					 [klass: AssertionError, message: "email addresses of 'to' field of message 1 Expected: every item is is \"address\" but: was <[address2]>"], 
					 [klass: AssertionError, message: "personal of 'to' field of message 1 Expected: every item is is \"personal\" but: was <[personal2]>"], 
					 [klass: AssertionError, message: "textual addresses of 'to' field of message 1 Expected: every item is is \"personal <address>\" but: was <[personal2 <address2>]>"], 
					 [klass: AssertionError, message: "address types of 'to' field of message 1 Expected: every item is is \"type\" but: was <[type2]>"]
					]
	}
	
	def "cc().address(#addressMacher) & cc().personal(#personalMacher) & cc().textual(#textualMacher) & cc().type(#typeMacher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			InternetAddress cc1 = Mock()
			message1.getRecipients(CC) >> ([cc1] as InternetAddress[])
			cc1.getAddress() >> "address1"
			cc1.getPersonal() >> "personal1"
			cc1.getType() >> "type1"
			cc1.toString() >> "personal1 <address1>"
			
			MimeMessage message2 = Mock()
			InternetAddress cc2 = Mock()
			message2.getRecipients(CC) >> ([cc2] as InternetAddress[])
			cc2.getAddress() >> "address2"
			cc2.getPersonal() >> "personal2"
			cc2.getType() >> "type2"
			cc2.toString() >> "personal2 <address2>"
			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg)
						.receivedMessages()
							.message(0)
								.cc()
									.address(addressMatcher)
									.personal(personalMatcher)
									.textual(textualMatcher)
									.type(typeMatcher)
									.and()
								.and()
							.message(1)
								.cc()
									.address(addressMatcher)
									.personal(personalMatcher)
									.textual(textualMatcher)
									.type(typeMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| addressMatcher					| personalMatcher					| textualMatcher														| typeMatcher					|| expected
			"should pass"			| everyItem(startsWith("address"))	| everyItem(startsWith("personal"))	| everyItem(allOf(startsWith("personal"), containsString("address")))	| everyItem(startsWith("type"))	||  []
			"should detect all"		| everyItem(is("address"))			| everyItem(is("personal"))			| everyItem(is("personal <address>"))									| everyItem(is("type"))			||  [
					 [klass: AssertionError, message: "email addresses of 'cc' field of message 0 Expected: every item is is \"address\" but: was <[address1]>"], 
					 [klass: AssertionError, message: "personal of 'cc' field of message 0 Expected: every item is is \"personal\" but: was <[personal1]>"], 
					 [klass: AssertionError, message: "textual addresses of 'cc' field of message 0 Expected: every item is is \"personal <address>\" but: was <[personal1 <address1>]>"], 
					 [klass: AssertionError, message: "address types of 'cc' field of message 0 Expected: every item is is \"type\" but: was <[type1]>"], 
					 [klass: AssertionError, message: "email addresses of 'cc' field of message 1 Expected: every item is is \"address\" but: was <[address2]>"], 
					 [klass: AssertionError, message: "personal of 'cc' field of message 1 Expected: every item is is \"personal\" but: was <[personal2]>"], 
					 [klass: AssertionError, message: "textual addresses of 'cc' field of message 1 Expected: every item is is \"personal <address>\" but: was <[personal2 <address2>]>"], 
					 [klass: AssertionError, message: "address types of 'cc' field of message 1 Expected: every item is is \"type\" but: was <[type2]>"]
					]
	}


	def "body(#bodyMatcher) & body().content(#contentMatcher) & body().contentAsString(#contentAsStringMatcher) & body().contentType(#contentTypeMatcher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			Multipart multipart1 = Mock()
			BodyPart bodyPart1 = Mock()
			message1.getContent() >> multipart1
			multipart1.getCount() >> 1
			multipart1.getBodyPart(0) >> bodyPart1
			bodyPart1.getInputStream() >> { new ByteArrayInputStream("body 1".getBytes("UTF-8")) }
			bodyPart1.getContentType() >> "text/plain"
			
			MimeMessage message2 = Mock()
			Multipart multipart2 = Mock()
			BodyPart bodyPart2 = Mock()
			message2.getContent() >> multipart2
			multipart2.getCount() >> 1
			multipart2.getBodyPart(0) >> bodyPart2
			bodyPart2.getInputStream() >> { new ByteArrayInputStream("body 2".getBytes("UTF-8")) }
			bodyPart2.getContentType() >> "text/html"
			
			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg)
						.receivedMessage(0)
							.body(bodyMatcher)
							.and()
						.receivedMessages()
							.message(0)
								.body()
									.content(contentMatcher)
									.contentAsString(contentAsStringMatcher)
									.contentType(contentTypeMatcher)
									.and()
								.and()
							.and()
						.receivedMessage(1)
							.body(bodyMatcher)
							.and()
						.receivedMessages()
							.message(1)
								.body()
									.content(contentMatcher)
									.contentAsString(contentAsStringMatcher)
									.contentType(contentTypeMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| bodyMatcher		| contentMatcher	| contentAsStringMatcher	| contentTypeMatcher		|| expected
			"should pass"			| notNullValue()	| notNullValue()	| containsString("body")	| startsWith("text/")		||  []
			"should detect all"		| nullValue()		| arrayWithSize(0)	| is("foo")					| is("application/html")	||  [
						 [klass: AssertionError, message: "body of message 0 Expected: null but: was <Mock for type 'BodyPart' named 'bodyPart1'>"],
						 [klass: AssertionError, message: "raw content of body of message 0 Expected: an array with size <0> but: was [<98>, <111>, <100>, <121>, <32>, <49>]"],
						 [klass: AssertionError, message: 'UTF-8 content of body of message 0 Expected: is "foo" but: was "body 1"'],
						 [klass: AssertionError, message: 'content-type of body of message 0 Expected: is "application/html" but: was "text/plain"'],
						 [klass: AssertionError, message: "body of message 1 Expected: null but: was <Mock for type 'BodyPart' named 'bodyPart2'>"],
						 [klass: AssertionError, message: "raw content of body of message 1 Expected: an array with size <0> but: was [<98>, <111>, <100>, <121>, <32>, <50>]"],
						 [klass: AssertionError, message: 'UTF-8 content of body of message 1 Expected: is "foo" but: was "body 2"'],
						 [klass: AssertionError, message: 'content-type of body of message 1 Expected: is "application/html" but: was "text/html"']
						]
	}
	
	def "alternative(#bodyMatcher) & alternative().content(#contentMatcher) & alternative().contentAsString(#contentAsStringMatcher) & alternative().contentType(#contentTypeMatcher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			Multipart multipart1 = Mock()
			BodyPart bodyPart1 = Mock()
			BodyPart bodyPart2 = Mock()
			message1.getContent() >> multipart1
			multipart1.getCount() >> 2
			multipart1.getBodyPart(0) >> bodyPart1
			multipart1.getBodyPart(1) >> bodyPart2
			bodyPart1.getInputStream() >> { new ByteArrayInputStream("alt 1".getBytes("UTF-8")) }
			bodyPart1.getContentType() >> "text/plain"
			bodyPart2.getInputStream() >> { new ByteArrayInputStream("<html><body>alt 1</body></html>".getBytes("UTF-8")) }
			bodyPart2.getContentType() >> "text/html"

			MimeMessage message2 = Mock()
			Multipart multipart2 = Mock()
			BodyPart bodyPart3 = Mock()
			BodyPart bodyPart4 = Mock()
			message2.getContent() >> multipart2
			multipart2.getCount() >> 2
			multipart2.getBodyPart(0) >> bodyPart3
			multipart2.getBodyPart(1) >> bodyPart4
			bodyPart3.getInputStream() >> { new ByteArrayInputStream("alt 2".getBytes("UTF-8")) }
			bodyPart3.getContentType() >> "text/plain"
			bodyPart4.getInputStream() >> { new ByteArrayInputStream("<html><body>alt 2</body></html>".getBytes("UTF-8")) }
			bodyPart4.getContentType() >> "text/html"

			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg)
						.receivedMessage(0)
							.alternative(bodyMatcher)
							.and()
						.receivedMessages()
							.message(0)
								.alternative()
									.content(contentMatcher)
									.contentAsString(contentAsStringMatcher)
									.contentType(contentTypeMatcher)
									.and()
								.and()
							.and()
						.receivedMessage(1)
							.alternative(bodyMatcher)
							.and()
						.receivedMessages()
							.message(1)
								.alternative()
									.content(contentMatcher)
									.contentAsString(contentAsStringMatcher)
									.contentType(contentTypeMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| bodyMatcher		| contentMatcher	| contentAsStringMatcher	| contentTypeMatcher		|| expected
			"should pass"			| notNullValue()	| notNullValue()	| containsString("alt")		| startsWith("text/")		||  []
			"should detect all"		| nullValue()		| arrayWithSize(0)	| is("foo")					| is("application/html")	||  [
					 [klass: AssertionError, message: "alternative of message 0 Expected: null but: was <Mock for type 'BodyPart' named 'bodyPart1'>"], 
					 [klass: AssertionError, message: "raw content of alternative of message 0 Expected: an array with size <0> but: was [<97>, <108>, <116>, <32>, <49>]"], 
					 [klass: AssertionError, message: 'UTF-8 content of alternative of message 0 Expected: is "foo" but: was "alt 1"'], 
					 [klass: AssertionError, message: 'content-type of alternative of message 0 Expected: is "application/html" but: was "text/plain"'], 
					 [klass: AssertionError, message: "alternative of message 1 Expected: null but: was <Mock for type 'BodyPart' named 'bodyPart3'>"], 
					 [klass: AssertionError, message: "raw content of alternative of message 1 Expected: an array with size <0> but: was [<97>, <108>, <116>, <32>, <50>]"], 
					 [klass: AssertionError, message: 'UTF-8 content of alternative of message 1 Expected: is "foo" but: was "alt 2"'], 
					 [klass: AssertionError, message: 'content-type of alternative of message 1 Expected: is "application/html" but: was "text/plain"']
					]
	}
	
	def "assertion on missing alternative"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			Multipart multipart1 = Mock()
			BodyPart bodyPart1 = Mock()
			message1.getContent() >> multipart1
			multipart1.getCount() >> 1
			multipart1.getBodyPart(0) >> bodyPart1
			bodyPart1.getInputStream() >> { new ByteArrayInputStream("alt 1".getBytes("UTF-8")) }
			bodyPart1.getContentType() >> "text/plain"

			greenMail.getReceivedMessages() >> ([message1] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg)
						.receivedMessage(0)
							.alternative(bodyMatcher)
							.and()
						.receivedMessages()
							.message(0)
								.alternative()
									.content(contentMatcher)
									.contentAsString(contentAsStringMatcher)
									.contentType(contentTypeMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc					| bodyMatcher		| contentMatcher	| contentAsStringMatcher	| contentTypeMatcher		|| expected
			"should pass"			| nullValue()		| nullValue()		| nullValue()				| nullValue()				||  []
			"should detect all"		| notNullValue()	| arrayWithSize(10)	| is("foo")					| is("application/html")	||  [
					 [klass: AssertionError, message: "alternative of message 0 Expected: not null but: was null"], 
					 [klass: AssertionError, message: "raw content of alternative of message 0 Expected: an array with size <10> but: was null"], 
					 [klass: AssertionError, message: 'UTF-8 content of alternative of message 0 Expected: is "foo" but: was null'], 
					 [klass: AssertionError, message: 'content-type of alternative of message 0 Expected: is "application/html" but: was null']
					]
	}

	
	def "attachment(#attachmentIndex | #attachmentName).filename(#nameMatcher) & attachment().description(#descriptionMatcher) & attachment().disposition(#dispositionMatcher) & attachment.header('Content-ID', #headerMatcher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage message1 = Mock()
			Multipart multipart1 = Mock()
			BodyPart attachment1 = Mock()
			BodyPart attachment2 = Mock()
			message1.getContent() >> multipart1
			multipart1.getCount() >> 2
			multipart1.getBodyPart(0) >> attachment1
			multipart1.getBodyPart(1) >> attachment2
			attachment1.getContentType() >> "application/pdf"
			attachment1.getFileName() >> "foo1.pdf"
			attachment1.getDescription() >> "desc1"
			attachment1.getDisposition() >> "dispo1"
			attachment1.getHeader("Content-ID") >> (["cid1"] as String[])
			attachment2.getContentType() >> "application/pdf"
			attachment2.getFileName() >> "foo2.pdf"
			attachment2.getDescription() >> "desc2"
			attachment2.getDisposition() >> "dispo2"
			attachment2.getHeader("Content-ID") >> (["cid2"] as String[])

			MimeMessage message2 = Mock()
			Multipart multipart2 = Mock()
			BodyPart attachment3 = Mock()
			BodyPart attachment4 = Mock()
			message2.getContent() >> multipart2
			multipart2.getCount() >> 2
			multipart2.getBodyPart(0) >> attachment3
			multipart2.getBodyPart(1) >> attachment4
			attachment3.getContentType() >> "application/x-pdf"
			attachment3.getFileName() >> "foo1.pdf"
			attachment3.getDescription() >> "desc3"
			attachment3.getDisposition() >> "dispo3"
			attachment3.getHeader("Content-ID") >> (["cid3"] as String[])
			attachment4.getContentType() >> "application/x-pdf"
			attachment4.getFileName() >> "foo2.pdf"
			attachment4.getDescription() >> "desc4"
			attachment4.getDisposition() >> "dispo4"
			attachment4.getHeader("Content-ID") >> (["cid4"] as String[])

			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessage(0).attachment(attachmentIndex)
							.filename(nameMatcher)
							.description(descriptionMatcher)
							.disposition(dispositionMatcher)
							.header("Content-ID", headerMatcher) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().message(0).attachment(attachmentName)
							.description(descriptionMatcher)
							.disposition(dispositionMatcher)
							.header("Content-ID", headerMatcher) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessage(1).attachment(attachmentIndex)
							.filename(nameMatcher)
							.description(descriptionMatcher)
							.disposition(dispositionMatcher)
							.header("Content-ID", headerMatcher) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().message(1).attachment(attachmentName)
							.description(descriptionMatcher)
							.disposition(dispositionMatcher)
							.header("Content-ID", headerMatcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc											| attachmentIndex	| attachmentName	| nameMatcher				| descriptionMatcher	| dispositionMatcher	| headerMatcher					|| expected
			"should pass"									| 0					| "foo1.pdf"		| containsString("foo")		| startsWith("desc")	| startsWith("dispo")	| contains(startsWith("cid"))	||  []
			"should detect all"								| 1					| "foo2.pdf"		| is("foo")					| is("desc")			| is("dispo")			| contains(is("cid"))			||  [
						 [klass: AssertionError, message: 'filename of attachment with index 1 of message 0 Expected: is "foo" but: was "foo2.pdf"'], 
						 [klass: AssertionError, message: 'description of attachment with index 1 of message 0 Expected: is "desc" but: was "desc2"'], 
						 [klass: AssertionError, message: 'disposition of attachment with index 1 of message 0 Expected: is "dispo" but: was "dispo2"'], 
						 [klass: AssertionError, message: 'header Content-ID of attachment with index 1 of message 0 Expected: iterable containing [is "cid"] but: was <[cid2]>'], 
						 [klass: AssertionError, message: 'description of attachment named \'foo2.pdf\' (matching index: 0) of message 0 Expected: is "desc" but: was "desc2"'], 
						 [klass: AssertionError, message: 'disposition of attachment named \'foo2.pdf\' (matching index: 0) of message 0 Expected: is "dispo" but: was "dispo2"'], 
						 [klass: AssertionError, message: 'header Content-ID of attachment named \'foo2.pdf\' (matching index: 0) of message 0 Expected: iterable containing [is "cid"] but: was <[cid2]>'], 
						 [klass: AssertionError, message: 'filename of attachment with index 1 of message 1 Expected: is "foo" but: was "foo2.pdf"'], 
						 [klass: AssertionError, message: 'description of attachment with index 1 of message 1 Expected: is "desc" but: was "desc4"'], 
						 [klass: AssertionError, message: 'disposition of attachment with index 1 of message 1 Expected: is "dispo" but: was "dispo4"'], 
						 [klass: AssertionError, message: 'header Content-ID of attachment with index 1 of message 1 Expected: iterable containing [is "cid"] but: was <[cid4]>'], 
						 [klass: AssertionError, message: 'description of attachment named \'foo2.pdf\' (matching index: 0) of message 1 Expected: is "desc" but: was "desc4"'], 
						 [klass: AssertionError, message: 'disposition of attachment named \'foo2.pdf\' (matching index: 0) of message 1 Expected: is "dispo" but: was "dispo4"'], 
						 [klass: AssertionError, message: 'header Content-ID of attachment named \'foo2.pdf\' (matching index: 0) of message 1 Expected: iterable containing [is "cid"] but: was <[cid4]>'], 
						]
			"should indicate attachment by index not found"	| 2					| "foo2.pdf"		| containsString("foo")		| startsWith("desc")	| startsWith("dispo")	| contains(startsWith("cid"))	||  [
						 [klass: AssertionError, message: 'filename of attachment with index 2 (/!\\ not found) of message 0 Expected: a string containing "foo" but: was null'],
						 [klass: AssertionError, message: 'description of attachment with index 2 (/!\\ not found) of message 0 Expected: a string starting with "desc" but: was null'],
						 [klass: AssertionError, message: 'disposition of attachment with index 2 (/!\\ not found) of message 0 Expected: a string starting with "dispo" but: was null'],
						 [klass: AssertionError, message: 'header Content-ID of attachment with index 2 (/!\\ not found) of message 0 Expected: iterable containing [a string starting with "cid"] but: was null'],
						 [klass: AssertionError, message: 'filename of attachment with index 2 (/!\\ not found) of message 1 Expected: a string containing "foo" but: was null'],
						 [klass: AssertionError, message: 'description of attachment with index 2 (/!\\ not found) of message 1 Expected: a string starting with "desc" but: was null'],
						 [klass: AssertionError, message: 'disposition of attachment with index 2 (/!\\ not found) of message 1 Expected: a string starting with "dispo" but: was null'],
						 [klass: AssertionError, message: 'header Content-ID of attachment with index 2 (/!\\ not found) of message 1 Expected: iterable containing [a string starting with "cid"] but: was null'],
						]
 			"should indicate attachment by name not found"	| 0					| "bar.pdf"			| containsString("foo")		| startsWith("desc")	| startsWith("dispo")	| contains(startsWith("cid"))	||  [
						 [klass: AssertionError, message: 'description of attachment named \'bar.pdf\' (/!\\ not found) of message 0 Expected: a string starting with "desc" but: was null'],
						 [klass: AssertionError, message: 'disposition of attachment named \'bar.pdf\' (/!\\ not found) of message 0 Expected: a string starting with "dispo" but: was null'],
						 [klass: AssertionError, message: 'header Content-ID of attachment named \'bar.pdf\' (/!\\ not found) of message 0 Expected: iterable containing [a string starting with "cid"] but: was null'],
						 [klass: AssertionError, message: 'description of attachment named \'bar.pdf\' (/!\\ not found) of message 1 Expected: a string starting with "desc" but: was null'],
						 [klass: AssertionError, message: 'disposition of attachment named \'bar.pdf\' (/!\\ not found) of message 1 Expected: a string starting with "dispo" but: was null'],
						 [klass: AssertionError, message: 'header Content-ID of attachment named \'bar.pdf\' (/!\\ not found) of message 1 Expected: iterable containing [a string starting with "cid"] but: was null'],
						]
	}
	
	def "attachments(#matcher) & #attachments.size() #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			
			MimeMessage message1 = Mock()
			Multipart multipart1 = Mock()
			message1.getContent() >> multipart1
			multipart1.getCount() >> attachments.size()
			multipart1.getBodyPart(0) >> attachments[0]
			multipart1.getBodyPart(1) >> attachments[1]

			MimeMessage message2 = Mock()
			Multipart multipart2 = Mock()
			message2.getContent() >> multipart2
			multipart2.getCount() >> attachments.size()
			multipart2.getBodyPart(0) >> attachments[0]
			multipart2.getBodyPart(1) >> attachments[1]

			greenMail.getReceivedMessages() >> ([message1, message2] as MimeMessage[])
			
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessage(0).attachments(matcher).and().receivedMessage(1).attachments(matcher) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().message(0).attachments(matcher).and().and().receivedMessages().message(1).attachments(matcher) },
				)
			}
		
		then:
			failures == expected
		
		where:
			desc															| attachments				| matcher					|| expected
			"should pass"													| []						| hasSize(0)				||  []
			"should pass"													| [attachment1()]			| hasSize(1)				||  []
			"should indicate that received attachments count is wrong"		| []						| hasSize(1)				||  [
						 [klass: AssertionError, message: 'attachments of message 0 Expected: a collection with size <1> but: was <[]>'], 
						 [klass: AssertionError, message: 'attachments of message 1 Expected: a collection with size <1> but: was <[]>'], 
						 [klass: AssertionError, message: 'attachments of message 0 Expected: a collection with size <1> but: was <[]>'], 
						 [klass: AssertionError, message: 'attachments of message 1 Expected: a collection with size <1> but: was <[]>']
						]
			"should indicate that received attachments are wrong"			| [attachment1()]			| hasItems(nullValue())		||  [
						 [klass: AssertionError, message: 'attachments of message 0 Expected: (a collection containing null) but: was <[Mock for type \'BodyPart\' named \'attachment\']>'],
						 [klass: AssertionError, message: 'attachments of message 1 Expected: (a collection containing null) but: was <[Mock for type \'BodyPart\' named \'attachment\']>'],
						 [klass: AssertionError, message: 'attachments of message 0 Expected: (a collection containing null) but: was <[Mock for type \'BodyPart\' named \'attachment\']>'],
						 [klass: AssertionError, message: 'attachments of message 1 Expected: (a collection containing null) but: was <[Mock for type \'BodyPart\' named \'attachment\']>']
						]
	}

	def "assertions on message #index but only #messages.size() messages received"() {
		given:
			GreenMailRule greenMail = Mock()
			greenMail.getReceivedMessages() >> (messages as MimeMessage[])
		
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessage(index) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().message(index) },
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
			[Mock(MimeMessage)]		| 1				||  [
														 [klass: AssertionError, message: "Assertions on message 1 can't be executed because 1 messages were received"],
														 [klass: AssertionError, message: "Assertions on message 1 can't be executed because 1 messages were received"],
														]
			[Mock(MimeMessage)]		| 2				||  [
														 [klass: AssertionError, message: "Assertions on message 2 can't be executed because 1 messages were received"],
														 [klass: AssertionError, message: "Assertions on message 2 can't be executed because 1 messages were received"],
														]
	}

	def "#messages.size() messages received & receivedMessages(#matcher) & count(#countMatcher) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			greenMail.getReceivedMessages() >> (messages as MimeMessage[])
		
		when:
			def failures = collectFailures { 
				assertAll(
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages(matcher) },
					(Consumer) { reg -> assertThat(greenMail, reg).receivedMessages().count(countMatcher) },
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
	
	private BodyPart attachment1() {
		BodyPart attachment = Mock()
		attachment.getContentType() >> "application/pdf"
		attachment.getFileName() >> "attachment1.pdf"
		return attachment
	}

	private BodyPart attachment2() {
		BodyPart attachment = Mock()
		attachment.getContentType() >> "application/pdf"
		attachment.getFileName() >> "attachment2.pdf"
		return attachment
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
