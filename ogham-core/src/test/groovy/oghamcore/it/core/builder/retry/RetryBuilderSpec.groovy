package oghamcore.it.core.builder.retry

import fr.sii.ogham.core.builder.context.DefaultBuildContext
import fr.sii.ogham.core.builder.retry.RetryBuilder
import fr.sii.ogham.core.builder.retry.RetryExecutorFactory
import fr.sii.ogham.core.retry.ExponentialDelayRetry
import fr.sii.ogham.core.retry.FixedDelayRetry
import fr.sii.ogham.core.retry.FixedIntervalRetry
import fr.sii.ogham.core.retry.PerExecutionDelayRetry
import fr.sii.ogham.core.retry.RetryExecutor
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
class RetryBuilderSpec extends Specification {
	def "FixedDelay[#delay|#maxRetries] should #desc"() {
		given:
			def strategy
			def factory = Mock(RetryExecutorFactory)
			factory.create(_, _) >> { p, a -> 
				strategy = p.provide()
				Mock(RetryExecutor)
			}
			def builder = new RetryBuilder(null, new DefaultBuildContext())
			builder
				.executor(factory)
				.fixedDelay()
					.delay(delay)
					.maxRetries(maxRetries)

		when:
			def executor = builder.build()
		
		then:
			strategy?.getClass() == expected
		
		where:
			desc				| delay	| maxRetries	|| expected
			"be disabled"		| null	| null			|| null
			"be disabled"		| 0		| null			|| null
			"be disabled"		| 9		| null			|| null
			"be disabled"		| null	| 0				|| null
			"be disabled"		| 0		| 0 			|| null
			"be disabled"		| 9		| 0				|| null
			"be disabled"		| null	| 5				|| null
			"be disabled"		| 0		| 5 			|| null
			"be enabled"		| 9		| 5				|| FixedDelayRetry
	}
	
	def "FixedInterval[#interval|#maxRetries] should #desc"() {
		given:
			def strategy
			def factory = Mock(RetryExecutorFactory)
			factory.create(_, _) >> { p, a -> 
				strategy = p.provide()
				Mock(RetryExecutor)
			}
			def builder = new RetryBuilder(null, new DefaultBuildContext())
			builder
				.executor(factory)
				.fixedInterval()
					.interval(interval)
					.maxRetries(maxRetries)

		when:
			def executor = builder.build()
		
		then:
			strategy?.getClass() == expected
		
		where:
			desc				| interval	| maxRetries	|| expected
			"be disabled"		| null		| null			|| null
			"be disabled"		| 0			| null			|| null
			"be disabled"		| 9			| null			|| null
			"be disabled"		| null		| 0				|| null
			"be disabled"		| 0			| 0 			|| null
			"be disabled"		| 9			| 0				|| null
			"be disabled"		| null		| 5				|| null
			"be disabled"		| 0			| 5 			|| null
			"be enabled"		| 9			| 5				|| FixedIntervalRetry
	}

	def "ExponentialDelay[#initialDelay|#maxRetries] should #desc"() {
		given:
			def strategy
			def factory = Mock(RetryExecutorFactory)
			factory.create(_, _) >> { p, a -> 
				strategy = p.provide()
				Mock(RetryExecutor)
			}
			def builder = new RetryBuilder(null, new DefaultBuildContext())
			builder
				.executor(factory)
				.exponentialDelay()
					.initialDelay(initialDelay)
					.maxRetries(maxRetries)

		when:
			def executor = builder.build()
		
		then:
			strategy?.getClass() == expected
		
		where:
			desc				| initialDelay	| maxRetries	|| expected
			"be disabled"		| null			| null			|| null
			"be disabled"		| 0				| null			|| null
			"be disabled"		| 9				| null			|| null
			"be disabled"		| null			| 0				|| null
			"be disabled"		| 0				| 0 			|| null
			"be disabled"		| 9				| 0				|| null
			"be disabled"		| null			| 5				|| null
			"be disabled"		| 0				| 5 			|| null
			"be enabled"		| 9				| 5				|| ExponentialDelayRetry
	}
	
	def "PerExecutionDelay[#delays|#maxRetries] should #desc"() {
		given:
			def strategy
			def factory = Mock(RetryExecutorFactory)
			factory.create(_, _) >> { p, a -> 
				strategy = p.provide()
				Mock(RetryExecutor)
			}
			def builder = new RetryBuilder(null, new DefaultBuildContext())
			builder
				.executor(factory)
				.perExecutionDelay()
					.delays(delays)
					.maxRetries(maxRetries)

		when:
			def executor = builder.build()
		
		then:
			strategy?.getClass() == expected
		
		where:
			desc				| delays		| maxRetries	|| expected
			"be disabled"		| null			| null			|| null
			"be disabled"		| []			| null			|| null
			"be disabled"		| [9L]			| null			|| null
			"be disabled"		| [9L, 8L]		| null			|| null
			"be disabled"		| null			| 0				|| null
			"be disabled"		| []			| 0				|| null
			"be disabled"		| [9L]			| 0				|| null
			"be disabled"		| [9L, 8L]		| 0				|| null
			"be disabled"		| null			| 5				|| null
			"be disabled"		| []			| 5				|| null
			"be disabled"		| [9L]			| 5				|| PerExecutionDelayRetry
			"be enabled"		| [9L, 8L]		| 5				|| PerExecutionDelayRetry
	}
	
	def "FixedDelay[#fixedDelay|#fixedDelayMaxRetries] & FixedInterval[#fixedInterval|#fixedIntervalMaxRetries] & ExponentialDelay[#exponentialDelay|#exponentialDelayMaxRetries] & PerExecutionDelay[#perExecutionDelays|#perExecutionDelayMaxRetries] should use #expected"() {
		given:
			def strategy
			def factory = Mock(RetryExecutorFactory)
			factory.create(_, _) >> { p, a -> 
				strategy = p.provide()
				Mock(RetryExecutor)
			}
			def builder = new RetryBuilder(null, new DefaultBuildContext())
			builder
				.executor(factory)
				.fixedDelay()
					.delay(fixedDelay)
					.maxRetries(fixedDelayMaxRetries)
					.and()
				.fixedInterval()
					.interval(fixedInterval)
					.maxRetries(fixedIntervalMaxRetries)
					.and()
				.exponentialDelay()
					.initialDelay(exponentialDelay)
					.maxRetries(exponentialDelayMaxRetries)
					.and()
				.perExecutionDelay()
					.delays(perExecutionDelays)
					.maxRetries(perExecutionDelayMaxRetries)

		when:
			def executor = builder.build()
		
		then:
			strategy?.getClass() == expected
		
		where:
			fixedDelay	| fixedDelayMaxRetries	| fixedInterval	| fixedIntervalMaxRetries	| exponentialDelay	| exponentialDelayMaxRetries	| perExecutionDelays	| perExecutionDelayMaxRetries	|| expected
			null		| null					| null			| null						| null				| null							| null					| null							|| null
			9			| 5						| null			| null						| null				| null							| null					| null							|| FixedDelayRetry
			null		| null					| 9				| 5							| null				| null							| null					| null							|| FixedIntervalRetry
			9			| 5						| 9				| 5							| null				| null							| null					| null							|| FixedIntervalRetry
			null		| null					| null			| null						| 9					| 5								| null					| null							|| ExponentialDelayRetry
			9			| 5						| null			| null						| 9					| 5								| null					| null							|| ExponentialDelayRetry
			null		| null					| 9				| 5							| 9					| 5								| null					| null							|| ExponentialDelayRetry
			9			| 5						| 9				| 5							| 9					| 5								| null					| null							|| ExponentialDelayRetry
			null		| null					| null			| null						| null				| null							| [9L]					| 5								|| PerExecutionDelayRetry
			9			| 5						| null			| null						| null				| null							| [9L]					| 5								|| PerExecutionDelayRetry
			null		| null					| 9				| 5							| null				| null							| [9L]					| 5								|| PerExecutionDelayRetry
			9			| 5						| 9				| 5							| null				| null							| [9L]					| 5								|| PerExecutionDelayRetry
			null		| null					| null			| null						| 9					| 5								| [9L]					| 5								|| PerExecutionDelayRetry
			9			| 5						| null			| null						| 9					| 5								| [9L]					| 5								|| PerExecutionDelayRetry
			null		| null					| 9				| 5							| 9					| 5								| [9L]					| 5								|| PerExecutionDelayRetry
			9			| 5						| 9				| 5							| 9					| 5								| [9L]					| 5								|| PerExecutionDelayRetry
	}
	
	// TODO: property evaluation with list
//	
//	def "default values"() {
//		
//	}
//	
//	def "configured values"() {
//		
//	}
	
	
	private void setProp(Properties props, String key, Object value) {
		if (value != null) {
			props.setProperty(key, String.valueOf(value))
		}
	}
}
