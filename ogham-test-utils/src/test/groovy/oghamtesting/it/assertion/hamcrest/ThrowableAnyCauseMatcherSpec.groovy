package oghamtesting.it.assertion.hamcrest

import static fr.sii.ogham.testing.assertion.AssertionHelper.assertThat
import static fr.sii.ogham.testing.assertion.OghamAssertions.isIdenticalHtml
import static fr.sii.ogham.testing.assertion.OghamAssertions.usingContext
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is

import java.lang.reflect.InvocationTargetException

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
class ThrowableAnyCauseMatcherSpec extends Specification {
	def "#finder should be found and #matcher should succeed"() {
		given:
			Exception exception = Mock()
			InvocationTargetException a = Mock()
			IllegalStateException b = Mock()
			IllegalArgumentException c = Mock()
			exception.getMessage() >> "level1: Exception"
			exception.getCause() >> a
			a.getMessage() >> "level2: InvocationTargetException"
			a.getCause() >> b
			b.getMessage() >> "level3: IllegalStateException"
			b.getCause() >> c
			c.getMessage() >> "level4: IllegalArgumentException"
			
		when:
			assertThat("", exception, hasAnyCause(finder, matcher))
		
		then:
			noExceptionThrown()
			
		where:
			finder									| matcher												
			IllegalStateException					| hasMessage("level3: IllegalStateException")
			IllegalStateException					| hasMessage(containsString("Illegal"))
			hasMessage(containsString("level4"))	| instanceOf(IllegalArgumentException)
			hasMessage(containsString("level"))		| instanceOf(Exception)
	}

	def "#finder / #matcher should fail with #expectedMessage"() {
		given:
			Exception exception = Mock()
			InvocationTargetException a = Mock()
			IllegalStateException b = Mock()
			IllegalArgumentException c = Mock()
			exception.getMessage() >> "level1: Exception"
			exception.getCause() >> a
			a.getMessage() >> "level2: InvocationTargetException"
			a.getCause() >> b
			b.getMessage() >> "level3: IllegalStateException"
			b.getCause() >> c
			c.getMessage() >> "level4: IllegalArgumentException"
			
		when:
			assertThat("", exception, hasAnyCause(finder, matcher))
		
		then:
			def e = thrown(AssertionError)
			e.getMessage() == expectedMessage
			
		where:
			finder						| matcher										|| expectedMessage
			NoSuchMethodException		| hasMessage(containsString("level"))			|| "\nExpected: exception with cause matching an instance of java.lang.NoSuchMethodException and matched cause exception with message a string containing \"level\"\n     but: was not found in exception stack"
			IllegalStateException		| hasMessage("message mismatch")				|| "\nExpected: exception with cause matching an instance of java.lang.IllegalStateException and matched cause exception with message is \"message mismatch\"\n     but: message was \"level3: IllegalStateException\""
	}
}
