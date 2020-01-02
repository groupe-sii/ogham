package oghamcore.ut.core.condition

import fr.sii.ogham.core.condition.Condition
import fr.sii.ogham.core.condition.OrCondition
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class NotConditionSpec extends Specification {

	def "#a or #b -> #expected"() {
		given:
			Condition condition1 = Mock()
			Condition condition2 = Mock()
			condition1.accept(_) >> a
			condition2.accept(_) >> b
			def or = new OrCondition(condition1, condition2)

		when:
			def result = or.accept(_)

		then:
			result.equals(expected)

		where:
			a		| b			|| expected
			false	| false		|| false
			false	| true		|| true
			true	| false		|| true
			true	| true		|| true
	}
}
