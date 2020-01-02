package oghamcore.ut.core.builder.configuration

import fr.sii.ogham.core.builder.configuration.MayOverride
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class MayOverrideSpec extends Specification {
	def "[overrideIfNonNull] #newValue #desc override #currentValue"() {
		given:
			def mayOverride = MayOverride.overrideIfNonNull(newValue)
			
		when:
			def result = mayOverride.override(currentValue);
			
		then:
			result == expected
			
		where:
			desc			| currentValue		| newValue			|| expected
			"should not"	| null				| null				|| null
			"should not"	| ""				| null				|| ""
			"should not"	| "current"			| null				|| "current"
			"should"		| null				| "new"				|| "new"
			"should"		| ""				| "new"				|| "new"
			"should"		| "current"			| "new"				|| "new"
	}

	def "[alwaysOverride] #newValue #desc override #currentValue"() {
		given:
			def mayOverride = MayOverride.alwaysOverride(newValue)
			
		when:
			def result = mayOverride.override(currentValue);
			
		then:
			result == expected
			
		where:
			desc			| currentValue		| newValue			|| expected
			"should"		| null				| null				|| null
			"should"		| ""				| null				|| null
			"should"		| "current"			| null				|| null
			"should"		| null				| "new"				|| "new"
			"should"		| ""				| "new"				|| "new"
			"should"		| "current"			| "new"				|| "new"
	}
	
	def "[overrideIfNotSet] #newValue #desc override #currentValue"() {
		given:
			def mayOverride = MayOverride.overrideIfNotSet(newValue)
			
		when:
			def result = mayOverride.override(currentValue);
			
		then:
			result == expected
			
		where:
			desc			| currentValue		| newValue			|| expected
			"should"		| null				| null				|| null
			"should not"	| ""				| null				|| ""
			"should not"	| "current"			| null				|| "current"
			"should"		| null				| "new"				|| "new"
			"should not"	| ""				| "new"				|| ""
			"should not"	| "current"			| "new"				|| "current"
	}

}
