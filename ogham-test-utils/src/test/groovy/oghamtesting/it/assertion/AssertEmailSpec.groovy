package oghamtesting.it.assertion

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertAll
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat
import static java.util.Arrays.asList
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
import javax.mail.Message
import javax.mail.Message.RecipientType
import javax.mail.Multipart
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

import org.junit.ComparisonFailure

import com.icegreen.greenmail.junit.GreenMailRule

import fr.sii.ogham.testing.assertion.email.AssertEmail
import fr.sii.ogham.testing.assertion.email.ExpectedContent
import fr.sii.ogham.testing.assertion.email.ExpectedEmail
import fr.sii.ogham.testing.assertion.email.ExpectedMultiPartEmail
import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertEmailSpec extends Specification {
	def setupSpec() {
		System.setProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "false");
	}
	
	def cleanupSpec() {
		System.clearProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure");
	}

	def "assertEquals(ExpectedEmail) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage actualEmail = Mock()
			actualEmail.getSubject() >> subject
			actualEmail.getFrom() >> [new InternetAddress(from)]
			actualEmail.getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
			actualEmail.getRecipients(TO) >> to.collect { new InternetAddress(it) }
			actualEmail.getRecipients(CC) >> cc.collect { new InternetAddress(it) }
			Multipart content = Mock()
			actualEmail.getContent() >> content
			BodyPart part = Mock()
			content.getCount() >> 1
			content.getBodyPart(0) >> part
			content.getContentType() >> "multipart/mixed"
			part.getContent() >> body
			part.getContentType() >> contentType

			def expectedEmail = new ExpectedEmail(
				"subject", 
				new ExpectedContent("body", "text/plain"), 
				"sender@yopmail.com", 
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures { 
				AssertEmail.assertEquals(expectedEmail, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject	| body		| contentType	| from					| to													| cc 										|| expected
			"should pass"			| "subject"	| "body"	| "text/plain"	| "sender@yopmail.com"	| ["recipient1@yopmail.com", "recipient2@yopmail.com"]	| ["cc1@yopmail.com", "cc2@yopmail.com"]	||  []
			"should detect all"		| "foo"		| "bar"		| "text/html"	| "sender@gmail.com"	| ["recipient1@gmail.com", "recipient2@gmail.com"]		| ["cc1@gmail.com", "cc2@gmail.com"]		||  [
				 [klass: ComparisonFailure, message: 'subject should be \'subject\' expected:<[subject]> but was:<[foo]>'], 
				 [klass: ComparisonFailure, message: 'from should be \'sender@yopmail.com\' expected:<sender@[yop]mail.com> but was:<sender@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<b[ody]> but was:<b[ar]>'], 
				 [klass: AssertionError, message: 'mimetype should match text/plain instead of text/html']
				]
	}

	def "assertEquals(ExpectedEmail, #actualEmailName) should not throw NPE and provide understandable message"() {
		given:
			def expectedEmail = new ExpectedEmail(
				"subject",
				new ExpectedContent("body", "text/plain"),
				"sender@yopmail.com",
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures {
				AssertEmail.assertEquals(expectedEmail, (Message[]) actual());
			}
		
		then:
			failures == expected
		
		where:
			actualEmailName	| actual				|| expected
			"null message"	| { [null] }				|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'], 
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should have 4 recipients expected:<4> but was:<null>'], 
					 [klass: AssertionError, message: 'should have 2 To expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should have 2 Cc expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'], 
					 [klass: IllegalStateException, message: 'Expected at least one body part but none found'], 
					 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<body> but was:<null>'], 
					 [klass: IllegalStateException, message: 'Expected at least one body part but none found'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null']
					]
			"empty mock"	| { [Mock(Message)] }	|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'], 
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should have 4 recipients expected:<4> but was:<null>'], 
					 [klass: AssertionError, message: 'should have 2 To expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should have 2 Cc expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'], 
					 [klass: IllegalStateException, message: 'Expected at least one body part but none found'], 
					 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<body> but was:<null>'], 
					 [klass: IllegalStateException, message: 'Expected at least one body part but none found'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null']
					]
	}
	
	def "assertSimilar(ExpectedEmail) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage actualEmail = Mock()
			actualEmail.getSubject() >> subject
			actualEmail.getFrom() >> [new InternetAddress(from)]
			actualEmail.getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
			actualEmail.getRecipients(TO) >> to.collect { new InternetAddress(it) }
			actualEmail.getRecipients(CC) >> cc.collect { new InternetAddress(it) }
			Multipart content = Mock()
			actualEmail.getContent() >> content
			BodyPart part = Mock()
			content.getCount() >> 1
			content.getBodyPart(0) >> part
			content.getContentType() >> "multipart/mixed"
			part.getContent() >> "<html><head></head><body>${body}</body></html>"
			part.getContentType() >> contentType

			def expectedEmail = new ExpectedEmail(
				"subject", 
				new ExpectedContent('<html><head></head><body><div a="1" b="2"></div></body></html>', "text/html"), 
				"sender@yopmail.com", 
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures { 
				AssertEmail.assertSimilar(expectedEmail, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject	| body							| contentType	| from					| to													| cc 										|| expected
			"should pass"			| "subject"	| ' <div b="2" a="1">\n</div> '	| "text/html"	| "sender@yopmail.com"	| ["recipient1@yopmail.com", "recipient2@yopmail.com"]	| ["cc1@yopmail.com", "cc2@yopmail.com"]	||  []
			"should detect all"		| "foo"		| '<div c="1" d="2"></div>' 	| "text/foo"	| "sender@gmail.com"	| ["recipient1@gmail.com", "recipient2@gmail.com"]		| ["cc1@gmail.com", "cc2@gmail.com"]		||  [
				 [klass: ComparisonFailure, message: 'subject should be \'subject\' expected:<[subject]> but was:<[foo]>'], 
				 [klass: ComparisonFailure, message: 'from should be \'sender@yopmail.com\' expected:<sender@[yop]mail.com> but was:<sender@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...d></head><body><div [a="1" b]="2"></div></body></...> but was:<...d></head><body><div [c="1" d]="2"></div></body></...>'],
				 [klass: AssertionError, message: 'mimetype should match text/html instead of text/foo']
				]
	}
	
	def "assertEquals(ExpectedMultiPartEmail) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage actualEmail = Mock()
			actualEmail.getSubject() >> subject
			actualEmail.getFrom() >> [new InternetAddress(from)]
			actualEmail.getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
			actualEmail.getRecipients(TO) >> to.collect { new InternetAddress(it) }
			actualEmail.getRecipients(CC) >> cc.collect { new InternetAddress(it) }
			Multipart content = Mock()
			actualEmail.getContent() >> content
			BodyPart part1 = Mock()
			BodyPart part2 = Mock()
			content.getCount() >> 2
			content.getBodyPart(0) >> part1
			content.getBodyPart(1) >> part2
			content.getContentType() >> "multipart/mixed"
			part1.getContent() >> alternative
			part1.getContentType() >> altContentType
			part2.getContent() >> "<html><head></head><body>${body}</body></html>"
			part2.getContentType() >> bodyContentType

			def expectedEmail = new ExpectedMultiPartEmail(
				"subject", 
				asList(new ExpectedContent("alternative", "text/plain"), 
					new ExpectedContent("<html><head></head><body><div a=1></div></body></html>", "text/html")), 
				"sender@yopmail.com", 
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures {
				AssertEmail.assertEquals(expectedEmail, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject	| body				| bodyContentType	| alternative	| altContentType	| from					| to													| cc 										|| expected
			"should pass"			| "subject"	| "<div a=1></div>"	| "text/html"		| "alternative"	| "text/plain"		| "sender@yopmail.com"	| ["recipient1@yopmail.com", "recipient2@yopmail.com"]	| ["cc1@yopmail.com", "cc2@yopmail.com"]	||  []
			"should detect all"		| "foo"		| "<p>bar</p>"		| "text/foo"		| "alt"			| "text/bar"		| "sender@gmail.com"	| ["recipient1@gmail.com", "recipient2@gmail.com"]		| ["cc1@gmail.com", "cc2@gmail.com"]		||  [
				 [klass: ComparisonFailure, message: 'subject should be \'subject\' expected:<[subject]> but was:<[foo]>'],
				 [klass: ComparisonFailure, message: 'from should be \'sender@yopmail.com\' expected:<sender@[yop]mail.com> but was:<sender@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alt[ernative]> but was:<alt[]>'], 
				 [klass: AssertionError, message: 'mimetype should match text/plain instead of text/bar'], 
				 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...<head></head><body><[div a=1></div]></body></html>> but was:<...<head></head><body><[p>bar</p]></body></html>>'], 
				 [klass: AssertionError, message: 'mimetype should match text/html instead of text/foo']
				]
	}
	
	def "assertSimilar(ExpectedMultiPartEmail) #desc"() {
		given:
			GreenMailRule greenMail = Mock()
			MimeMessage actualEmail = Mock()
			actualEmail.getSubject() >> subject
			actualEmail.getFrom() >> [new InternetAddress(from)]
			actualEmail.getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
			actualEmail.getRecipients(TO) >> to.collect { new InternetAddress(it) }
			actualEmail.getRecipients(CC) >> cc.collect { new InternetAddress(it) }
			Multipart content = Mock()
			actualEmail.getContent() >> content
			BodyPart part1 = Mock()
			BodyPart part2 = Mock()
			content.getCount() >> 2
			content.getBodyPart(0) >> part1
			content.getBodyPart(1) >> part2
			content.getContentType() >> "multipart/mixed"
			part1.getContent() >> alternative
			part1.getContentType() >> altContentType
			part2.getContent() >> "<html><head></head><body>${body}</body></html>"
			part2.getContentType() >> bodyContentType

			def expectedEmail = new ExpectedMultiPartEmail(
				"subject",
				asList(new ExpectedContent("alternative", "text/plain"),
					new ExpectedContent("<html><head></head><body><div a=1 b=2></div></body></html>", "text/html")),
				"sender@yopmail.com",
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures {
				AssertEmail.assertSimilar(expectedEmail, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject	| body							| bodyContentType	| alternative	| altContentType	| from					| to													| cc 										|| expected
			"should pass"			| "subject"	| ' <div b="2" a="1">\n</div> '	| "text/html"		| "alternative"	| "text/plain"		| "sender@yopmail.com"	| ["recipient1@yopmail.com", "recipient2@yopmail.com"]	| ["cc1@yopmail.com", "cc2@yopmail.com"]	||  []
			"should detect all"		| "foo"		| '<div c="2" d="1"></div>'		| "text/foo"		| "alt"			| "text/bar"		| "sender@gmail.com"	| ["recipient1@gmail.com", "recipient2@gmail.com"]		| ["cc1@gmail.com", "cc2@gmail.com"]		||  [
				 [klass: ComparisonFailure, message: 'subject should be \'subject\' expected:<[subject]> but was:<[foo]>'],
				 [klass: ComparisonFailure, message: 'from should be \'sender@yopmail.com\' expected:<sender@[yop]mail.com> but was:<sender@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alt[ernative]> but was:<alt[]>'],
				 [klass: AssertionError, message: 'mimetype should match text/plain instead of text/bar'],
				 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...d></head><body><div [a=1 b=2]></div></body></html...> but was:<...d></head><body><div [c="2" d="1"]></div></body></html...>'],
				 [klass: AssertionError, message: 'mimetype should match text/html instead of text/foo']
				]
	}
	
	
	def "assertEquals(ExpectedMultiPartEmail, #actualEmailName) should not throw NPE and provide understandable message"() {
		given:
			def expectedEmail = new ExpectedMultiPartEmail(
				"subject",
				asList(new ExpectedContent("alternative", "text/plain"),
					new ExpectedContent("<html><head></head><body><div a=1 b=2></div></body></html>", "text/html")),
				"sender@yopmail.com",
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures {
				AssertEmail.assertEquals(expectedEmail, (Message[]) actual());
			}
		
		then:
			failures == expected
		
		where:
			actualEmailName	| actual				|| expected
			"null message"	| { [null] }				|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'],
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'],
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should have 4 recipients expected:<4> but was:<null>'],
					 [klass: AssertionError, message: 'should have 2 To expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should have 2 Cc expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: AssertionError, message: 'should have 2 parts expected:<2> but was:<0>'], 
					 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alternative> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null'], 
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<<html><head></head><body><div a=1 b=2></div></body></html>> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/html instead of null']
					]
			"empty mock"	| { [Mock(Message)] }	|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'],
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'],
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should have 4 recipients expected:<4> but was:<null>'],
					 [klass: AssertionError, message: 'should have 2 To expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should have 2 Cc expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: AssertionError, message: 'should have 2 parts expected:<2> but was:<0>'], 
					 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alternative> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null'], 
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<<html><head></head><body><div a=1 b=2></div></body></html>> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/html instead of null']
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
			def message = t.getMessage()
			assertions += [klass: t.getClass(), message: message==null ? null : message.replaceAll("\\s+", " ")];
		}
		return assertions
	}
}
