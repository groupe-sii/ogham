package oghamtesting.ut.util.port

import java.util.function.IntPredicate

import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.util.port.DefaultPortFinder
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class DefaultPortFinderSpec extends Specification {
	def "findAvailablePort(#minPort, #maxPort) with #availablePorts should return #expected port"() {
		given:
			def currentPort = 0
			IntPredicate portAvailable = Mock() {
				test(_) >> { int p -> availablePorts.contains(p) }
			}
			Random random = Mock() {
				nextInt(_) >> { currentPort++ }
			}
			def finder = new DefaultPortFinder("", portAvailable, random)
		
		when:
			def available = finder.findAvailablePort(minPort, maxPort)
		
		then:
			expected == available
		
		where:
			minPort		| maxPort		| availablePorts		|| expected
			1024		| 65535			| [1024]				|| 1024
			1024		| 65535			| [3000]				|| 3000
			1024		| 65535			| [65535]				|| 65535
			1024		| 65535			| [1500, 3000]			|| 1500
			1024		| 65535			| [500, 1500, 3000]		|| 1500
			8000		| 9000			| [8000]				|| 8000
			8000		| 9000			| [8500]				|| 8500
			8000		| 9000			| [9000]				|| 9000
			8000		| 9000			| [8200, 8500]			|| 8200
			8000		| 9000			| [7999, 8200, 8500]	|| 8200
			8000		| 9000			| [7999, 8999]			|| 8999
	}
	
	def "findAvailablePort(#minPort, #maxPort) with #availablePorts should fail"() {
		given:
			def currentPort = 0
			IntPredicate portAvailable = Mock() {
				test(_) >> { int p -> availablePorts.contains(p) }
			}
			Random random = Mock() {
				nextInt(_) >> { currentPort++ }
			}
			def finder = new DefaultPortFinder("", portAvailable, random)
		
		when:
			finder.findAvailablePort(minPort, maxPort)
		
		then:
			def e = thrown(expected)
		
		where:
			minPort		| maxPort		| availablePorts	|| expected
			1024		| 65535			| []				|| IllegalStateException
			1024		| 65535			| [1023]			|| IllegalStateException
			1024		| 65535			| [65536]			|| IllegalStateException
			8000		| 9000			| []				|| IllegalStateException
			8000		| 9000			| [7999]			|| IllegalStateException
			8000		| 9000			| [9001]			|| IllegalStateException
			-1			| 9000			| []				|| IllegalArgumentException
			2000		| 65536			| []				|| IllegalArgumentException
			2000		| 1000			| []				|| IllegalArgumentException
	}

	def "findAvailablePorts(#numRequested, #minPort, #maxPort) with #availablePorts should return #expected"() {
		given:
			def currentPort = 0
			IntPredicate portAvailable = Mock() {
				test(_) >> { int p -> availablePorts.contains(p) }
			}
			Random random = Mock() {
				nextInt(_) >> { currentPort++ }
			}
			def finder = new DefaultPortFinder("", portAvailable, random)
		
		when:
			def available = finder.findAvailablePorts(numRequested, minPort, maxPort)
		
		then:
			expected == new ArrayList(available)
		
		where:
			minPort		| maxPort		| numRequested	| availablePorts			|| expected
			1024		| 65535			| 1				| [1024]					|| [1024]
			1024		| 65535			| 1				| [3000]					|| [3000]
			1024		| 65535			| 1				| [65535]					|| [65535]
			1024		| 65535			| 1				| [1500, 3000]				|| [1500]
			1024		| 65535			| 1				| [500, 1500, 3000]			|| [1500]
			1024		| 65535			| 1				| [500, 1500, 3000, 4000]	|| [1500]
			1024		| 65535			| 1				| [500, 1500, 3000, 4000]	|| [1500]
			1024		| 65535			| 2				| [1500, 3000]				|| [1500, 3000]
			1024		| 65535			| 2				| [500, 1500, 3000]			|| [1500, 3000]
			1024		| 65535			| 2				| [500, 1500, 3000, 4000]	|| [1500, 3000]
			1024		| 65535			| 3				| [500, 1500, 3000, 4000]	|| [1500, 3000, 4000]
			8000		| 9000			| 1				| [8000]					|| [8000]
			8000		| 9000			| 1				| [8500]					|| [8500]
			8000		| 9000			| 1				| [9000]					|| [9000]
			8000		| 9000			| 1				| [8200, 8500]				|| [8200]
			8000		| 9000			| 1				| [7999, 8200, 8500]		|| [8200]
			8000		| 9000			| 1				| [7999, 8999]				|| [8999]
			8000		| 9000			| 2				| [8200, 8500]				|| [8200, 8500]
			8000		| 9000			| 2				| [7999, 8200, 8500]		|| [8200, 8500]
	}
	
	def "findAvailablePorts(#numRequested, #minPort, #maxPort) with #availablePorts should fail"() {
		given:
			def currentPort = 0
			IntPredicate portAvailable = Mock() {
				test(_) >> { int p -> availablePorts.contains(p) }
			}
			Random random = Mock() {
				nextInt(_) >> { currentPort++ }
			}
			def finder = new DefaultPortFinder("", portAvailable, random)
		
		when:
			finder.findAvailablePorts(numRequested, minPort, maxPort)
		
		then:
			def e = thrown(expected)
		
		where:
			minPort		| maxPort		| numRequested	| availablePorts				|| expected
			1024		| 65535			| 1				| []							|| IllegalStateException
			1024		| 65535			| 1				| [1023]						|| IllegalStateException
			1024		| 65535			| 1				| [65536]						|| IllegalStateException
			1024		| 65535			| 1				| [1000, 68000]					|| IllegalStateException
			1024		| 65535			| 3				| [1500, 3000]					|| IllegalStateException
			1024		| 65535			| 3				| [500, 1500, 3000]				|| IllegalStateException
			1024		| 65535			| 4				| [500, 1500, 3000, 4000]		|| IllegalStateException
			8000		| 9000			| 1				| []							|| IllegalStateException
			8000		| 9000			| 1				| [7999]						|| IllegalStateException
			8000		| 9000			| 1				| [9001]						|| IllegalStateException
			8000		| 9000			| 1				| [7000, 7999, 9001, 10000]		|| IllegalStateException
			8000		| 9000			| 4				| [8000, 8001, 8002]			|| IllegalStateException
			-1			| 65535			| 1				| []							|| IllegalArgumentException
			2000		| 65636			| 1				| []							|| IllegalArgumentException
			2000		| 100			| 1				| []							|| IllegalArgumentException
			2000		| 3000			| 0				| []							|| IllegalArgumentException
			2000		| 2010			| 11			| []							|| IllegalArgumentException
	}
}
