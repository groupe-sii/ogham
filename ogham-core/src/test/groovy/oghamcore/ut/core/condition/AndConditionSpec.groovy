package oghamcore.ut.core.condition

import fr.sii.ogham.core.condition.AndCondition
import fr.sii.ogham.core.condition.Condition
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class AndConditionSpec extends Specification {

	def "#a and #b -> #expected"() {
		given:
			Condition condition1 = Mock()
			Condition condition2 = Mock()
			condition1.accept(_) >> a
			condition2.accept(_) >> b
			def and = new AndCondition(condition1, condition2)

		when:
			def result = and.accept(_)

		then:
			result.equals(expected)

		where:
			a		| b			|| expected
			false	| false		|| false
			false	| true		|| false
			true	| false		|| false
			true	| true		|| true
	}
}
