package oghamtesting.it.assertion

import org.opentest4j.AssertionFailedError

import fr.sii.ogham.testing.assertion.template.AssertTemplate
import fr.sii.ogham.testing.assertion.util.MultipleAssertionError
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertTemplateSpec extends Specification {
	def setupSpec() {
		System.setProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "false");
	}
	
	def cleanupSpec() {
		System.clearProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure");
	}
	
	def "assertEquals('#path', '#content') #desc"() {
		when:
			def failures = collectFailures {
				AssertTemplate.assertEquals(path, (Object) content);
			}
			
		then:
			failures == expected
		
		where:
			desc										| path				| content					|| expected
			"shoud pass"								| "expected.txt"	| "template content"		|| []
			"shoud detect differences"					| "expected.txt"	| "foo"						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <foo>']
						]
			"shoud detect differences with new lines"	| "expected.txt"	| "\ntemplate content\n"	|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: < template content >']
						]
			"load expected content error"				| "not found"		| "foo"						|| [
						 [klass: FileNotFoundException, message: 'No resource found for path \'not found\''], 
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <null> but was: <foo>']
						]
			"null path"									| null				| "foo"						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <null> but was: <foo>']
						]
			"null content"								| "expected.txt"	| null						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <null>']
						]
	}
	
	
	def "assertSimilar('#path', '#content') #desc"() {
		when:
			def failures = collectFailures {
				AssertTemplate.assertSimilar(path, (Object) content);
			}
			
		then:
			failures == expected
		
		where:
			desc										| path				| content					|| expected
			"shoud pass"								| "expected.txt"	| "template content"		|| []
			"shoud detect differences"					| "expected.txt"	| "foo"						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <foo>']
						]
			"shoud skip differences with new lines"		| "expected.txt"	| "\ntemplate content\n"	|| []
			"load expected content error"				| "not found"		| "foo"						|| [
						 [klass: FileNotFoundException, message: 'No resource found for path \'not found\''],
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <null> but was: <foo>']
						]
			"null path"									| null				| "foo"						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <null> but was: <foo>']
						]
			"null content"								| "expected.txt"	| null						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <null>']
						]
	}

	
	def "assertEquals('#str', '#content') #desc"() {
		when:
			def failures = collectFailures {
				AssertTemplate.assertEquals(str, (String) content);
			}
			
		then:
			failures == expected
		
		where:
			desc										| str					| content					|| expected
			"shoud pass"								| "template content"	| "template content"		|| []
			"shoud detect differences"					| "template content"	| "foo"						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <foo>']
						]
			"shoud detect differences with new lines"	| "template content"	| "\ntemplate content\n"	|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: < template content >']
						]
			"shoud detect differences with new lines"	| "\ntemplate content\n"| "template content"		|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: < template content > but was: <template content>']
						]
			"null content"								| "template content"	| null						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <null>']
						]
	}
	
	
	def "assertSimilar('#str', '#content') #desc"() {
		when:
			def failures = collectFailures {
				AssertTemplate.assertSimilar(str, (String) content);
			}
			
		then:
			failures == expected
		
		where:
			desc										| str					| content					|| expected
			"shoud pass"								| "template content"	| "template content"		|| []
			"shoud detect differences"					| "template content"	| "foo"						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <foo>']
						]
			"shoud skip differences with new lines"		| "template content"	| "\ntemplate content\n"	|| []
			"null content"								| "template content"	| null						|| [
						 [klass: AssertionFailedError, message: 'parsed template is different to expected content ==> expected: <template content> but was: <null>']
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
