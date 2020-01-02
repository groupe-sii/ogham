package oghamcore.ut.core.util

import fr.sii.ogham.core.util.ArrayUtils
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class ArrayUtilsSpec extends Specification {
	def "concat(#first, #others) -> #expected"() {
		when:
			def result = ArrayUtils.concat(first, others as Integer[])
			
		then:
			result == expected
		
		where:
			first		| others			|| expected
			1			| []				|| [1]
			1			| [null]			|| [1, null]
			1			| [2, 3]			|| [1, 2, 3]
			1			| [2, 3, 4]			|| [1, 2, 3, 4]
	}
	
	def "concat(#first, #second) -> #expected"() {
		when:
			def result = ArrayUtils.concat(first as Integer[], second as Integer[])
			
		then:
			result == expected
		
		where:
			first		| second			|| expected
			[]			| [null]			|| [null]
			[null]		| [null]			|| [null, null]
			[]			| []				|| []
			[]			| [10]				|| [10]
			[]			| [10, 11]			|| [10, 11]
			[1]			| []				|| [1]
			[1]			| [10]				|| [1, 10]
			[1]			| [10, 11]			|| [1, 10, 11]
			[1, 2]		| []				|| [1, 2]
			[1, 2]		| [10]				|| [1, 2, 10]
			[1, 2]		| [10, 11]			|| [1, 2, 10, 11]
	}

	def "concat(#first, #second) -> not supported"() {
		when:
			ArrayUtils.concat(first as Integer[], second as Integer[])
			
		then:
			thrown(IllegalArgumentException)
		
		where:
			first		| second
			null		| null
			[]			| null
			null		| []
	}
}
