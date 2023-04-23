package oghamtesting.it.assertion

import static java.nio.charset.StandardCharsets.UTF_8
import static java.util.Arrays.asList
import static jakarta.mail.Message.RecipientType.BCC
import static jakarta.mail.Message.RecipientType.CC
import static jakarta.mail.Message.RecipientType.TO

import jakarta.mail.BodyPart
import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage

import org.junit.ComparisonFailure

import fr.sii.ogham.testing.assertion.email.AssertEmail
import fr.sii.ogham.testing.assertion.email.ExpectedContent
import fr.sii.ogham.testing.assertion.email.ExpectedEmail
import fr.sii.ogham.testing.assertion.email.ExpectedEmailHeader
import fr.sii.ogham.testing.assertion.email.ExpectedMultiPartEmail
import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
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
			MimeMessage actualEmail = Mock {
				Multipart content = Mock {
					BodyPart part = Mock {
						getInputStream() >> { new ByteArrayInputStream(body.getBytes(UTF_8)) }
						getContentType() >> contentType
						isMimeType("text/*") >> true
					}
					getCount() >> 1
					getBodyPart(0) >> part
					getContentType() >> "multipart/mixed"
				}
				getSubject() >> subject
				getFrom() >> [new InternetAddress(from)]
				getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
				getRecipients(TO) >> to.collect { new InternetAddress(it) }
				getRecipients(CC) >> cc.collect { new InternetAddress(it) }
				getContent() >> content
			}

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
				 [klass: ComparisonFailure, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<b[ody]> but was:<b[ar]>'], 
				 [klass: AssertionError, message: 'mimetype should match text/plain instead of text/html']
				]
	}

	def "assertEquals|assertSimilar(ExpectedEmail, #actualEmailName) should not throw NPE and provide understandable message"() {
		given:
			def expectedEmail = new ExpectedEmail(
				"subject",
				new ExpectedContent("body", "text/plain"),
				"sender@yopmail.com",
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures(
				{ AssertEmail.assertEquals(expectedEmail, (Message[]) actual()) },
				{ AssertEmail.assertSimilar(expectedEmail, (Message[]) actual()) }
			)
		
		then:
			failures == expected
		
		where:
			actualEmailName		| actual								|| expected
			"null message"		| { [null] }							|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'], 
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'Expected at least one body part but none found'], 
					 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<body> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null']
					] * 2
			"empty mock"		| { [Mock(Message)] }					|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'], 
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'Expected at least one body part but none found'], 
					 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<body> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null']
					] * 2
			"no message"		| { [] }								|| [
					 [klass: AssertionError, message: 'should have received 1 email expected:<1> but was:<0>'], 
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'], 
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'Expected at least one body part but none found'], 
					 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<body> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null']
					] * 2
			"several messages"	| { [Mock(Message), Mock(Message)] }	|| [
					 [klass: AssertionError, message: 'should have received 1 email expected:<1> but was:<2>'], 
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'], 
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'], 
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'Expected at least one body part but none found'], 
					 [klass: ComparisonFailure, message: 'body should be \'body\' expected:<body> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null']
					] * 2
	}
	
	def "assertSimilar(ExpectedEmail) #desc"() {
		given:
			MimeMessage actualEmail = Mock {
				Multipart content = Mock {
					BodyPart part = Mock {
						getInputStream() >> { new ByteArrayInputStream("<html><head></head><body>${body}</body></html>".getBytes(UTF_8)) }
						getContentType() >> contentType
						isMimeType("text/*") >> true
					}
					getCount() >> 1
					getBodyPart(0) >> part
					getContentType() >> "multipart/mixed"
					isMimeType("text/*") >> false
				}
				getContent() >> content
				getSubject() >> subject
				getFrom() >> [new InternetAddress(from)]
				getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
				getRecipients(TO) >> to.collect { new InternetAddress(it) }
				getRecipients(CC) >> cc.collect { new InternetAddress(it) }
			}

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
				 [klass: ComparisonFailure, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'], 
				 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...d></head><body><div [a="1" b]="2"></div></body></...> but was:<...d></head><body><div [c="1" d]="2"></div></body></...>'],
				 [klass: AssertionError, message: 'mimetype should match text/html instead of text/foo']
				]
	}
	
	def "assertEquals(ExpectedMultiPartEmail) #desc"() {
		given:
			MimeMessage actualEmail = Mock {
				Multipart content = Mock {
					BodyPart part1 = Mock {
						getContent() >> alternative
						getContentType() >> altContentType
						isMimeType("text/*") >> true
					}
					BodyPart part2 = Mock {
						getContent() >> "<html><head></head><body>${body}</body></html>"
						getContentType() >> bodyContentType
						isMimeType("text/*") >> true
					}
					getCount() >> 2
					getBodyPart(0) >> part1
					getBodyPart(1) >> part2
					getContentType() >> "multipart/mixed"
					isMimeType("text/*") >> false
				}
				getSubject() >> subject
				getFrom() >> [new InternetAddress(from)]
				getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
				getRecipients(TO) >> to.collect { new InternetAddress(it) }
				getRecipients(CC) >> cc.collect { new InternetAddress(it) }
				getContent() >> content
			}

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
				 [klass: ComparisonFailure, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alt[ernative]> but was:<alt[]>'], 
				 [klass: AssertionError, message: 'mimetype should match text/plain instead of text/bar'], 
				 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...<head></head><body><[div a=1></div]></body></html>> but was:<...<head></head><body><[p>bar</p]></body></html>>'], 
				 [klass: AssertionError, message: 'mimetype should match text/html instead of text/foo']
				]
	}
	
	def "assertSimilar(ExpectedMultiPartEmail) #desc"() {
		given:
			MimeMessage actualEmail = Mock {
				Multipart content = Mock {
					BodyPart part1 = Mock {
						getContent() >> alternative
						getContentType() >> altContentType
						isMimeType("text/*") >> true
					}
					BodyPart part2 = Mock {
						getContent() >> "<html><head></head><body>${body}</body></html>"
						getContentType() >> bodyContentType
						isMimeType("text/*") >> true
					}
					getCount() >> 2
					getBodyPart(0) >> part1
					getBodyPart(1) >> part2
					getContentType() >> "multipart/mixed"
					isMimeType("text/*") >> false
				}
				getSubject() >> subject
				getFrom() >> [new InternetAddress(from)]
				getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) }
				getRecipients(TO) >> to.collect { new InternetAddress(it) }
				getRecipients(CC) >> cc.collect { new InternetAddress(it) }
				getContent() >> content
			}

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
				 [klass: ComparisonFailure, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'],
				 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alt[ernative]> but was:<alt[]>'],
				 [klass: AssertionError, message: 'mimetype should match text/plain instead of text/bar'],
				 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...d></head><body><div [a=1 b=2]></div></body></html...> but was:<...d></head><body><div [c="2" d="1"]></div></body></html...>'],
				 [klass: AssertionError, message: 'mimetype should match text/html instead of text/foo']
				]
	}
	
	
	def "assertEquals|assertSimilar(ExpectedMultiPartEmail, #actualEmailName) should not throw NPE and provide understandable message"() {
		given:
			def expectedEmail = new ExpectedMultiPartEmail(
				"subject",
				asList(new ExpectedContent("alternative", "text/plain"),
					new ExpectedContent("<html><head></head><body><div a=1 b=2></div></body></html>", "text/html")),
				"sender@yopmail.com",
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedEmail.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			
		when:
			def failures = collectFailures(
				{ AssertEmail.assertEquals(expectedEmail, (Message[]) actual()) },
				{ AssertEmail.assertSimilar(expectedEmail, (Message[]) actual()) }
			)
		
		then:
			failures == expected
		
		where:
			actualEmailName		| actual								|| expected
			"null message"		| { [null] }							|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'],
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'],
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: AssertionError, message: 'should have 2 parts expected:<2> but was:<0>'], 
					 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alternative> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null'], 
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<<html><head></head><body><div a=1 b=2></div></body></html>> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/html instead of null']
					] * 2
			"empty mock"		| { [Mock(Message)] }					|| [
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'],
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'],
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: AssertionError, message: 'should have 2 parts expected:<2> but was:<0>'], 
					 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alternative> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null'], 
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<<html><head></head><body><div a=1 b=2></div></body></html>> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/html instead of null']
					] * 2
			"no message"		| { [] }								|| [
					 [klass: AssertionError, message: 'should have received 1 email expected:<1> but was:<0>'], 
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'],
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'],
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: AssertionError, message: 'should have 2 parts expected:<2> but was:<0>'], 
					 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alternative> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null'], 
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<<html><head></head><body><div a=1 b=2></div></body></html>> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/html instead of null']
					] * 2
			"several messages"	| { [Mock(Message), Mock(Message)] }	|| [
					 [klass: AssertionError, message: 'should have received 1 email expected:<1> but was:<2>'], 
					 [klass: AssertionError, message: 'subject should be \'subject\' expected:<subject> but was:<null>'],
					 [klass: AssertionError, message: 'should have only one from expected:<1> but was:<null>'],
					 [klass: AssertionError, message: 'from should be \'sender@yopmail.com\' expected:<sender@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 4 recipients expected:<4> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: AssertionError, message: 'should have 2 parts expected:<2> but was:<0>'], 
					 [klass: ComparisonFailure, message: 'body[0] should be \'alternative\' expected:<alternative> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/plain instead of null'], 
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<<html><head></head><body><div a=1 b=2></div></body></html>> but was:<null>'], 
					 [klass: AssertionError, message: 'mimetype should match text/html instead of null']
					] * 2
	}
	
	def "assertBody(#expectedBody, #actualBody, #strict) #desc"() {
		when:
			def failures = collectFailures {
				AssertEmail.assertBody(expectedBody, actualBody, strict)
			}
		
		then:
			failures == expected
			
		where:
			desc									| expectedBody								| actualBody						| strict	|| expected
			"should pass"							| "foo"										| "foo"								| true		|| []
			"should pass"							| "foo"										| "foo"								| false		|| []
			"should detect different spaces"		| " foo "									| "foo"								| true		|| [
					 [klass: ComparisonFailure, message: 'body should be \' foo \' expected:<[ foo ]> but was:<[foo]>']
					]
			"should detect different spaces"		| " foo "									| "foo"								| false		|| [
					 [klass: ComparisonFailure, message: 'body should be \' foo \' expected:<[ foo ]> but was:<[foo]>']
					]
			"should detect different new lines"		| "\nfoo\n"									| "foo"								| true		|| [
					 [klass: ComparisonFailure, message: 'body should be \' foo \' expected:<[ foo ]> but was:<[foo]>']
					]
			"should ignore different spaces"		| "\nfoo\n"									| "foo"								| false		|| []
			"should pass"							| wrapHtml('<div a="1" b="2">')				| wrapHtml('<div a="1" b="2">')		| true		|| []
			"should pass"							| wrapHtml('<div a="1" b="2">')				| wrapHtml('<div b=2 a=1 >')		| false		|| []
			"should detect different attributes"	| wrapHtml('<div a="1" b="2" c="3">')		| wrapHtml('<div b=2 a=1>')			| true		|| [
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...ead> <body> <div [a="1" b="2" c="3"]> <body> <html>> but was:<...ead> <body> <div [b=2 a=1]> <body> <html>>']
					]
			"should detect different attributes"	| wrapHtml('<div a="1" b="2" c="3">')		| wrapHtml('<div b=2 a=1>')			| false		|| [
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...ead> <body> <div [a="1" b="2" c="3"]> <body> <html>> but was:<...ead> <body> <div [b=2 a=1]> <body> <html>>']
					]
			"should detect different order"			| wrapHtml('<div></div><p></p>')			| wrapHtml('<p></p><div></div>')	| true		|| [
					 [klass: ComparisonFailure, message: 'HTML element different to expected one. See logs for details about found differences. expected:<...></head> <body> <[div></div><p></p]> <body> <html>> but was:<...></head> <body> <[p></p><div></div]> <body> <html>>']
					]
			"should ignore different order"			| wrapHtml('<div></div><p></p>')			| wrapHtml('<p></p><div></div>')	| false		|| []
	}
	
	def "assertHeaders() #desc"() {
		given:
			MimeMessage actualEmail = Mock {
				getSubject() >> subject
				getFrom() >> [new InternetAddress(from)]
				getAllRecipients() >> to.collect { new InternetAddress(it) } + cc.collect { new InternetAddress(it) } + bcc.collect { new InternetAddress(it) }
				getRecipients(TO) >> to.collect { new InternetAddress(it) }
				getRecipients(CC) >> cc.collect { new InternetAddress(it) }
				getRecipients(BCC) >> bcc.collect { new InternetAddress(it) }
			}
			
			def expectedHeaders = new ExpectedEmailHeader(
				"subject",
				"sender@yopmail.com",
				"recipient1@yopmail.com", "recipient2@yopmail.com")
			expectedHeaders.setCc("cc1@yopmail.com", "cc2@yopmail.com")
			expectedHeaders.setBcc("bcc1@yopmail.com", "bcc2@yopmail.com")
			
		when:
			def failures = collectFailures {
				AssertEmail.assertHeaders(expectedHeaders, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| subject	| from					| to																				| cc 														| bcc 															|| expected
			"should pass"			| "subject"	| "sender@yopmail.com"	| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								| ["cc1@yopmail.com", "cc2@yopmail.com"]					| ["bcc1@yopmail.com", "bcc2@yopmail.com"]						||  []
			"should detect all"		| "foo"		| "sender@gmail.com"	| ["recipient1@gmail.com", "recipient2@gmail.com"]									| ["cc1@gmail.com", "cc2@gmail.com"]						| ["bcc1@gmail.com", "bcc2@gmail.com"]							||  [
					 [klass: ComparisonFailure, message: 'subject should be \'subject\' expected:<[subject]> but was:<[foo]>'],
					 [klass: ComparisonFailure, message: 'from should be \'sender@yopmail.com\' expected:<sender@[yop]mail.com> but was:<sender@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@[yop]mail.com> but was:<cc1@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@[yop]mail.com> but was:<cc2@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient Bcc[0] should be \'bcc1@yopmail.com\' expected:<bcc1@[yop]mail.com> but was:<bcc1@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient Bcc[1] should be \'bcc2@yopmail.com\' expected:<bcc2@[yop]mail.com> but was:<bcc2@[g]mail.com>'],
					]
			"no recipient"			| "subject"	| "sender@yopmail.com"	| []																				| []														| []															||  [
					 [klass: AssertionError, message: 'should be received by 6 recipients expected:<6> but was:<0>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<0>'], 
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<0>'], 
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'cc1@yopmail.com\' expected:<cc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Bcc) expected:<2> but was:<0>'], 
					 [klass: AssertionError, message: 'recipient Bcc[0] should be \'bcc1@yopmail.com\' expected:<bcc1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Bcc[1] should be \'bcc2@yopmail.com\' expected:<bcc2@yopmail.com> but was:<null>']
					]
			"missing recipient"		| "subject"	| "sender@yopmail.com"	| ["recipient1@yopmail.com"]														| ["cc1@yopmail.com"]										| ["bcc1@yopmail.com"]											||  [
					 [klass: AssertionError, message: 'should be received by 6 recipients expected:<6> but was:<3>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<1>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<1>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'cc2@yopmail.com\' expected:<cc2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Bcc) expected:<2> but was:<1>'], 
					 [klass: AssertionError, message: 'recipient Bcc[1] should be \'bcc2@yopmail.com\' expected:<bcc2@yopmail.com> but was:<null>']
					]
			"too many recipients"	| "subject"	| "sender@yopmail.com"	| ["recipient1@yopmail.com", "recipient2@yopmail.com", "recipient3@yopmail.com"]	| ["cc1@yopmail.com", "cc2@yopmail.com", "cc3@yopmail.com"]	| ["bcc1@yopmail.com", "bcc2@yopmail.com", "bcc3@yopmail.com"]	||  [
					 [klass: AssertionError, message: 'should be received by 6 recipients expected:<6> but was:<9>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<3>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<3>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Bcc) expected:<2> but was:<3>']
					]
	}
	
	def "assertRecipients(#expectedRecipients, #recipients) #desc"() {
		given:
			MimeMessage actualEmail = Mock {
				getAllRecipients() >> (recipients * 3).collect { new InternetAddress(it) }
				getRecipients(TO) >> recipients.collect { new InternetAddress(it) }
				getRecipients(CC) >> recipients.collect { new InternetAddress(it) }
				getRecipients(BCC) >> recipients.collect { new InternetAddress(it) }
			}
		when:
			def failures = collectFailures(
				{ AssertEmail.assertRecipients(expectedRecipients, actualEmail, TO) },
				{ AssertEmail.assertRecipients(expectedRecipients, actualEmail, CC) },
				{ AssertEmail.assertRecipients(expectedRecipients, actualEmail, BCC) }
			)
		
		then:
			failures == expected
		
		where:
			desc					| expectedRecipients																| recipients																		|| expected
			"should pass"			| []																				| []																				||  []
			"no expected recipient"	| []																				| ["recipient1@yopmail.com"]														||  [
					 [klass: AssertionError, message: 'should be received by no recipients (of type RecipientType.To)'], 
					 [klass: AssertionError, message: 'should be received by no recipients (of type RecipientType.Cc)'], 
					 [klass: AssertionError, message: 'should be received by no recipients (of type RecipientType.Bcc)']
					]
			"should pass"			| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								||  []
			"should detect"			| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								| ["recipient1@gmail.com", "recipient2@gmail.com"]									||  [
					 [klass: ComparisonFailure, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
					 [klass: ComparisonFailure, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient Cc[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
					 [klass: ComparisonFailure, message: 'recipient Cc[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>'],
					 [klass: ComparisonFailure, message: 'recipient Bcc[0] should be \'recipient1@yopmail.com\' expected:<recipient1@[yop]mail.com> but was:<recipient1@[g]mail.com>'], 
					 [klass: ComparisonFailure, message: 'recipient Bcc[1] should be \'recipient2@yopmail.com\' expected:<recipient2@[yop]mail.com> but was:<recipient2@[g]mail.com>']
					]
			"no recipient"			| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								| []																				||  [
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<0>'], 
					 [klass: AssertionError, message: 'recipient To[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<0>'], 
					 [klass: AssertionError, message: 'recipient Cc[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Bcc) expected:<2> but was:<0>'], 
					 [klass: AssertionError, message: 'recipient Bcc[0] should be \'recipient1@yopmail.com\' expected:<recipient1@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'recipient Bcc[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>']
					]
			"missing recipient"		| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								| ["recipient1@yopmail.com"]														||  [
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<1>'], 
					 [klass: AssertionError, message: 'recipient To[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<1>'], 
					 [klass: AssertionError, message: 'recipient Cc[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Bcc) expected:<2> but was:<1>'], 
					 [klass: AssertionError, message: 'recipient Bcc[1] should be \'recipient2@yopmail.com\' expected:<recipient2@yopmail.com> but was:<null>']
					]
			"too many recipients"	| ["recipient1@yopmail.com", "recipient2@yopmail.com"]								| ["recipient1@yopmail.com", "recipient2@yopmail.com", "recipient3@yopmail.com"]	||  [
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.To) expected:<2> but was:<3>'], 
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Cc) expected:<2> but was:<3>'],
					 [klass: AssertionError, message: 'should be received by 2 recipients (of type RecipientType.Bcc) expected:<2> but was:<3>']
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
			def message = t.getMessage()
			assertions += [klass: t.getClass(), message: message==null ? null : message.replaceAll("\\s+", " ")];
		}
		return assertions
	}
	
	private String wrapHtml(String content) {
		if (content == null) {
			return null
		}
		return "<html>\n\t<head></head>\n\t<body>\n\t\t${content}\n\t<body>\n<html>"
	}

}
