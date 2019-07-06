package fr.sii.ogham.ut.core.condition

import java.util.regex.Pattern

import org.junit.Rule

import fr.sii.ogham.core.condition.PropertyPatternCondition
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.junit.LoggingTestRule
import spock.lang.Specification

class PropertyPatternConditionSpec extends Specification {
	@Rule
	LoggingTestRule logging;

	def "value #propertyValue for #key that #matches #pattern should return #expected"() {
		given:
			PropertyResolver resolver = Mock()
			resolver.getProperty(_) >> propertyValue

		when:
			def result = new PropertyPatternCondition(key, Pattern.compile(pattern), resolver).accept(null)

		then:
			result.equals(expected)

		where:
			matches              | key | pattern | propertyValue || expected
			"doesn't match"      | "a" | "fo.+"  |  null         || false
			"matches"            | "b" | "fo.+"  | "foo"         || true
			"doesn't match"      | "c" | "fo.+"  | "bar"         || false
	}
}
