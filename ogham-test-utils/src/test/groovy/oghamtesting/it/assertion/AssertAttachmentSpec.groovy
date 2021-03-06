package oghamtesting.it.assertion

import javax.mail.BodyPart
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.internet.MimeMessage

import org.junit.ComparisonFailure
import org.junit.internal.ArrayComparisonFailure

import fr.sii.ogham.testing.assertion.email.AssertAttachment
import fr.sii.ogham.testing.assertion.email.ExpectedAttachment
import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertAttachmentSpec extends Specification {
	def setupSpec() {
		System.setProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "false");
	}
	
	def cleanupSpec() {
		System.clearProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure");
	}

	def "assertEquals() #desc"() {
		given:
			MimeMessage actualEmail = Mock {
				Multipart multipart = Mock {
					BodyPart part = Mock {
						getInputStream() >> { new ByteArrayInputStream(content.getBytes("UTF-8")) }
						getContentType() >> contentType
						getFileName() >> filename
						getDescription() >> description
						getDisposition() >> disposition
					}
					getCount() >> 1
					getBodyPart(0) >> part
					getContentType() >> "multipart/mixed"
				}
				getContent() >> multipart
			}

			def expectedAttachment = new ExpectedAttachment("attachment.pdf", "application/pdf", "content".getBytes("UTF-8"), "description", "disposition")
			
		when:
			def failures = collectFailures { 
				AssertAttachment.assertEquals(expectedAttachment, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| content	| contentType		| filename			| description	| disposition	|| expected
			"should pass"			| "content"	| "application/pdf"	| "attachment.pdf"	| "description"	| "disposition"	|| []
			"attachment not found"	| "content"	| "application/pdf"	| "foo.pdf"			| "description"	| "disposition"	|| [
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) mimetype should match \'application/pdf\' but was null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) description should be \'description\' expected:<description> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) disposition should be \'disposition\' expected:<disposition> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) has invalid content: actual array was null']
					]
			"should detect all"		| "foo"		| "bar"				| "attachment.pdf"	| "baz"			| "foobar"		|| [
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' mimetype should match \'application/pdf\' but was \'bar\''], 
					 [klass: ComparisonFailure, message: 'attachment named \'attachment.pdf\' description should be \'description\' expected:<[description]> but was:<[baz]>'], 
					 [klass: ComparisonFailure, message: 'attachment named \'attachment.pdf\' disposition should be \'disposition\' expected:<[disposition]> but was:<[foobar]>'], 
					 [klass: ArrayComparisonFailure, message: 'attachment named \'attachment.pdf\' has invalid content: array lengths differed, expected.length=7 actual.length=3; arrays first differed at element [0]; expected:<99> but was:<102>']
					]
	}

	def "assertEquals(#desc) should not throw NPE and provide understandable message"() {
		given:
			def expectedAttachment = new ExpectedAttachment("attachment.pdf", "application/pdf", "content".getBytes("UTF-8"), "description", "disposition")
			
		when:
			def failures = collectFailures {
				AssertAttachment.assertEquals(expectedAttachment, (Message[]) actual());
			}
		
		then:
			failures == expected
		
		where:
			desc					| actual										|| expected
			"null message"			| { [null] }									|| [
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: IllegalStateException, message: 'The multipart can\'t be null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) mimetype should match \'application/pdf\' but was null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) description should be \'description\' expected:<description> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) disposition should be \'disposition\' expected:<disposition> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) has invalid content: actual array was null']
					]
			"empty mock"			| { [Mock(MimeMessage)] }						|| [
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: IllegalStateException, message: 'The multipart can\'t be null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) mimetype should match \'application/pdf\' but was null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) description should be \'description\' expected:<description> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) disposition should be \'disposition\' expected:<disposition> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) has invalid content: actual array was null']
					]
			"no messages"			| { [] }										|| [
					 [klass: AssertionError, message: 'should have only one message expected:<1> but was:<0>'], 
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: IllegalStateException, message: 'The multipart can\'t be null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) mimetype should match \'application/pdf\' but was null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) description should be \'description\' expected:<description> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) disposition should be \'disposition\' expected:<disposition> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) has invalid content: actual array was null']
					]
			"several messages"		| { [Mock(MimeMessage), Mock(MimeMessage)] }	|| [
					 [klass: AssertionError, message: 'should have only one message expected:<1> but was:<2>'],
					 [klass: AssertionError, message: 'should be multipart message'], 
					 [klass: IllegalStateException, message: 'The multipart can\'t be null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) mimetype should match \'application/pdf\' but was null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) description should be \'description\' expected:<description> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) disposition should be \'disposition\' expected:<disposition> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) has invalid content: actual array was null']
					]
	}
	
	def "assertEquals() on unreadable attachment should not fail and report all errors"() {
		given:
			MimeMessage actualEmail = Mock {
				Multipart multipart = Mock {
					BodyPart part = Mock()
					getCount() >> 1
					getBodyPart(0) >> { throw new MessagingException("unreadable") }
					getContentType() >> "multipart/mixed"
				}
				getContent() >> multipart
			}

			def expectedAttachment = new ExpectedAttachment("attachment.pdf", "application/pdf", "content".getBytes("UTF-8"), "description", "disposition")
			
		when:
			def failures = collectFailures {
				AssertAttachment.assertEquals(expectedAttachment, actualEmail);
			}
		
		then:
			failures == expected
		
		where:
			desc					| content	| contentType		| filename			| description	| disposition	|| expected
			"should detect all"		| "foo"		| "bar"				| "attachment.pdf"	| "baz"			| "foobar"		|| [
					 [klass: MessagingException, message: 'unreadable'],
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) mimetype should match \'application/pdf\' but was null'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) description should be \'description\' expected:<description> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) disposition should be \'disposition\' expected:<disposition> but was:<null>'], 
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' (/!\\ not found) has invalid content: actual array was null']
					]
	}

	def "assertEquals(BodyPart) #desc"() {
		given:
			BodyPart part = Mock {
				getInputStream() >> { new ByteArrayInputStream(content.getBytes("UTF-8")) }
				getContentType() >> contentType
				getFileName() >> filename
				getDescription() >> description
				getDisposition() >> disposition
			}

			def expectedAttachment = new ExpectedAttachment("attachment.pdf", "application/pdf", "content".getBytes("UTF-8"), "description", "disposition")
			
		when:
			def failures = collectFailures {
				AssertAttachment.assertEquals(expectedAttachment, part);
			}
		
		then:
			failures == expected
		
		where:
			desc					| content	| contentType		| filename			| description	| disposition	|| expected
			"should pass"			| "content"	| "application/pdf"	| "attachment.pdf"	| "description"	| "disposition"	|| []
			"should detect all"		| "foo"		| "bar"				| "attachment.pdf"	| "baz"			| "foobar"		|| [
					 [klass: AssertionError, message: 'attachment named \'attachment.pdf\' mimetype should match \'application/pdf\' but was \'bar\''],
					 [klass: ComparisonFailure, message: 'attachment named \'attachment.pdf\' description should be \'description\' expected:<[description]> but was:<[baz]>'],
					 [klass: ComparisonFailure, message: 'attachment named \'attachment.pdf\' disposition should be \'disposition\' expected:<[disposition]> but was:<[foobar]>'],
					 [klass: ArrayComparisonFailure, message: 'attachment named \'attachment.pdf\' has invalid content: array lengths differed, expected.length=7 actual.length=3; arrays first differed at element [0]; expected:<99> but was:<102>']
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
