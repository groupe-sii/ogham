package oghamcore.ut.core.condition

import fr.sii.ogham.core.condition.Condition
import fr.sii.ogham.core.condition.NotCondition
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class OrConditionSpec extends Specification {

	def "not(#a) -> #expected"() {
		given:
			Condition condition1 = Mock()
			condition1.accept(_) >> a
			def not = new NotCondition(condition1)

		when:
			def result = not.accept(_)

		then:
			result.equals(expected)

		where:
			a		|| expected
			false	|| true
			true	|| false
	}
}
