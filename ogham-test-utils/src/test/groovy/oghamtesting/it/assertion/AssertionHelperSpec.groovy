package oghamtesting.it.assertion

import static fr.sii.ogham.testing.assertion.AssertionHelper.assertThat
import static fr.sii.ogham.testing.assertion.OghamAssertions.isIdenticalHtml
import static fr.sii.ogham.testing.assertion.OghamAssertions.usingContext
import static org.hamcrest.Matchers.is

import org.hamcrest.Matcher
import org.junit.ComparisonFailure

import fr.sii.ogham.testing.assertion.AssertionHelper
import fr.sii.ogham.testing.assertion.OghamAssertions
import fr.sii.ogham.testing.assertion.context.Context
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertionHelperSpec extends Specification {
	def "assertThat(\"#reason\", actual, #matcher) matcher should fail with #expectedException: #expectedMessage"() {
		when:
			assertThat(reason, "foo", matcher)
		
		then:
			def e = thrown(expectedException)
			e.getMessage() == expectedMessage
			
		where:
			reason				| matcher													|| expectedException	| expectedMessage
			""					| is("bar")													|| AssertionError		| "\nExpected: is \"bar\"\n     but: was \"foo\""
			""					| isIdenticalHtml("bar")									|| ComparisonFailure	| "\nThe two HTML documents are not identical.\nHere are the differences found:\n  - Expected text value 'bar' but was 'foo' - comparing <body ...>bar</body> at /html[1]/body[1]/text()[1] to <body ...>foo</body> at /html[1]/body[1]/text()[1]\n\n\nExpected: \"bar\"\n     but: was \"foo\" expected:<[bar]> but was:<[foo]>"
			""					| usingContext("\${name}", ctx(), is("bar"))				|| AssertionError		| "reason from context\nExpected: is \"bar\"\n     but: was \"foo\""
			""					| usingContext("\${name}", ctx(), isIdenticalHtml("bar"))	|| ComparisonFailure	| "reason from context\nThe two HTML documents are not identical.\nHere are the differences found:\n  - Expected text value 'bar' but was 'foo' - comparing <body ...>bar</body> at /html[1]/body[1]/text()[1] to <body ...>foo</body> at /html[1]/body[1]/text()[1]\n\n\nExpected: \"bar\"\n     but: was \"foo\" expected:<[bar]> but was:<[foo]>"
			"reason"			| is("bar")													|| AssertionError		| "reason\nExpected: is \"bar\"\n     but: was \"foo\""
			"reason"			| isIdenticalHtml("bar")									|| ComparisonFailure	| "reason\nThe two HTML documents are not identical.\nHere are the differences found:\n  - Expected text value 'bar' but was 'foo' - comparing <body ...>bar</body> at /html[1]/body[1]/text()[1] to <body ...>foo</body> at /html[1]/body[1]/text()[1]\n\n\nExpected: \"bar\"\n     but: was \"foo\" expected:<[bar]> but was:<[foo]>"
			"reason"			| usingContext("\${name}", ctx(), is("bar"))				|| AssertionError		| "reason\nExpected: is \"bar\"\n     but: was \"foo\""
			"reason"			| usingContext("\${name}", ctx(), isIdenticalHtml("bar"))	|| ComparisonFailure	| "reason\nThe two HTML documents are not identical.\nHere are the differences found:\n  - Expected text value 'bar' but was 'foo' - comparing <body ...>bar</body> at /html[1]/body[1]/text()[1] to <body ...>foo</body> at /html[1]/body[1]/text()[1]\n\n\nExpected: \"bar\"\n     but: was \"foo\" expected:<[bar]> but was:<[foo]>"
	}
	
	def ctx() {
		Context ctx = Mock()
		ctx.evaluate(_) >> "reason from context"
		return ctx
	}
}
