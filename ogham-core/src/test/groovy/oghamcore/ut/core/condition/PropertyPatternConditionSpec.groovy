package oghamcore.ut.core.condition

import java.util.regex.Pattern

import fr.sii.ogham.core.condition.PropertyPatternCondition
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class PropertyPatternConditionSpec extends Specification {

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
