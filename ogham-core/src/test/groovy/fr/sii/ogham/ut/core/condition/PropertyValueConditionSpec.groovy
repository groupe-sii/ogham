package fr.sii.ogham.ut.core.condition

import org.junit.Rule

import fr.sii.ogham.core.condition.PropertyValueCondition
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.junit.LoggingTestRule
import spock.lang.Specification

class PropertyValueConditionSpec extends Specification {
	@Rule
	LoggingTestRule logging;

	def "value #propertyValue for #key that #matches #matchValue should return #expected"() {
		given:
			PropertyResolver resolver = Mock()
			resolver.getProperty(_) >> propertyValue

		when:
			def result = new PropertyValueCondition(key, matchValue, resolver).accept(null)

		then:
			result.equals(expected)

		where:
			matches              | key | matchValue | propertyValue || expected
			"doesn't match"      | "a" | "foo"      | null          || false
			"matches"            | "b" | "foo"      | "foo"         || true
			"doesn't match"      | "c" | "foo"      | "foobar"      || false
	}
}
