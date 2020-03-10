package oghamcore.it.core.configuration

import static fr.sii.ogham.core.builder.configuration.MayOverride.overrideIfNotSet
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.AFTER_INIT
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.BEFORE_BUILD

import fr.sii.ogham.core.builder.BuildContext
import fr.sii.ogham.core.builder.MessagingBuilder
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper
import fr.sii.ogham.core.builder.configurer.ConfigurationPhase
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.core.exception.MessagingException
import fr.sii.ogham.core.message.Message
import fr.sii.ogham.core.service.MessagingService
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class ConfigurationLifecycleSpec extends Specification {
	def "[no default value | no optional value] #desc should be #expected"() {
		given:
			System.clearProperty("after-init")
			System.clearProperty("before-build.service-provider")
			System.clearProperty("before-build")
			/** 1) instantiate builder (use empty() here to manually test complete lifecycle) */
			def builder = new TestMessagingBuilder()
			/** 2) find all configurers for "standard" configuration (see MessagingBuilder.standard()) */
			// simulate MessagingBuilder.findAndRegister(builder, "standard") behavior:
			builder.register(new AfterInitConfigurer(false, null), 200, AFTER_INIT)
			builder.register(new BeforeBuildServiceProviderConfigurer(false, null), 500, BEFORE_BUILD)
			builder.register(new BeforeBuildDefaultConfigurer(false, null), 100, BEFORE_BUILD)
			/** 3) trigger ConfigurationPhase.AFTER_INIT configuration */
			builder.configure(ConfigurationPhase.AFTER_INIT)
			//   => calls AfterInitConfigurer.configure()
			/** 4) developer configures Ogham for its needs */
			//    4a) developer can set property values from everywhere
			setSystemProperty("after-init", afterInitProp);
			setSystemProperty("before-build.service-provider", beforeBuildServiceProviderProp);
			setSystemProperty("before-build", beforeBuildProp);
			
			System.out.println()
			System.out.println("after-init="+afterInitProp)
			System.out.println("before-build.service-provider="+beforeBuildServiceProviderProp)
			System.out.println("before-build="+beforeBuildProp)

			builder
				.environment()
					// system properties
					.systemProperties()
			//    4b) developer can customize parts of Ogham in its code
			builder
				.confValue(developerCodeValue)
			/** 5) developer has finished configuring so he calls .build() method to get instance of MessagingBuilder */
			//    5a) trigger ConfigurationPhase.BEFORE_BUILD configuration
			builder.configure(ConfigurationPhase.BEFORE_BUILD)
			//    => some configuration may be applied (service providers for example)
			//    => some configuration may be applied (default configuration)
			
		when:
			//    5b) MessagingService is created with merged configuration
			TestMessagingService service = builder.build()
			System.out.println("confValue="+service.getConfValue())
			System.out.println("expected="+expected)

		then:
			service.getConfValue() == expected

		where:
			desc																	| afterInitProp 				| beforeBuildServiceProviderProp 					| beforeBuildProp 				| developerCodeValue 	|| expected
			"no developer value and no property value set" 							| null 							| null 												| null 							| null 					|| null
			"no developer value and after init property set" 						| "value from \${after-init}"	| null 												| null 							| null 					|| "value from \${after-init}"
			"no developer value and service provider property set" 					| null							| "value from \${before-build.service-provider}"	| null 							| null 					|| "value from \${before-build.service-provider}"
			"no developer value and after init and service provider property set" 	| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| null 							| null 					|| "value from \${after-init}"
			
			"no developer value and before build property set"	 					| null 							| null 												| "value from \${before-build}" | null 					|| "value from \${before-build}"
			"no developer value and after init and before build property set" 		| "value from \${after-init}"	| null 												| "value from \${before-build}" | null 					|| "value from \${after-init}"
			"no developer value and service provider and before build property set"	| null							| "value from \${before-build.service-provider}"	| "value from \${before-build}" | null 					|| "value from \${before-build.service-provider}"
			"no developer value and all properties set" 							| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| "value from \${before-build}" | null 					|| "value from \${after-init}"

			"developer value set and no property value set" 						| null 							| null 												| null 							| "developer value"		|| "developer value"
			"developer value set and after init property set" 						| "value from \${after-init}"	| null 												| null 							| "developer value"		|| "developer value"
			"developer value set and service provider property set" 				| null							| "value from \${before-build.service-provider}"	| null 							| "developer value"		|| "developer value"
			"developer value set and after init and service provider property set" 	| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| null 							| "developer value"		|| "developer value"
			
			"developer value set and before build property set" 					| null 							| null 												| "value from \${before-build}" | "developer value"		|| "developer value"
			"developer value set and after init and before build property set"		| "value from \${after-init}"	| null 												| "value from \${before-build}" | "developer value"		|| "developer value"
			"developer value set and service provider and before build property set"| null							| "value from \${before-build.service-provider}"	| "value from \${before-build}" | "developer value"		|| "developer value"
			"developer value set and all properties set"						 	| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| "value from \${before-build}" | "developer value"		|| "developer value"
	}


	def "[default value | no optional value] #desc should be #expected"() {
		given:
			System.clearProperty("after-init")
			System.clearProperty("before-build.service-provider")
			System.clearProperty("before-build")
			/** 1) instantiate builder (use empty() here to manually test complete lifecycle) */
			def builder = new TestMessagingBuilder()
			/** 2) find all configurers for "standard" configuration (see MessagingBuilder.standard()) */
			// simulate MessagingBuilder.findAndRegister(builder, "standard") behavior:
			builder.register(new AfterInitConfigurer(true, null), 200, AFTER_INIT)
			builder.register(new BeforeBuildServiceProviderConfigurer(true, null), 500, BEFORE_BUILD)
			builder.register(new BeforeBuildDefaultConfigurer(true, null), 100, BEFORE_BUILD)
			/** 3) trigger ConfigurationPhase.AFTER_INIT configuration */
			builder.configure(ConfigurationPhase.AFTER_INIT)
			//   => calls AfterInitConfigurer.configure()
			/** 4) developer configures Ogham for its needs */
			//    4a) developer can set property values from everywhere
			System.out.println()
			System.out.println("after-init="+afterInitProp)
			System.out.println("before-build.service-provider="+beforeBuildServiceProviderProp)
			System.out.println("before-build="+beforeBuildProp)
			
			setSystemProperty("after-init", afterInitProp);
			setSystemProperty("before-build.service-provider", beforeBuildServiceProviderProp);
			setSystemProperty("before-build", beforeBuildProp);
			builder
				.environment()
					// system properties
					.systemProperties()
			//    4b) developer can customize parts of Ogham in its code
			builder
				.confValue(developerCodeValue)
			/** 5) developer has finished configuring so he calls .build() method to get instance of MessagingBuilder */
			//    5a) trigger ConfigurationPhase.BEFORE_BUILD configuration
			builder.configure(ConfigurationPhase.BEFORE_BUILD)
			//    => some configuration may be applied (service providers for example)
			//    => some configuration may be applied (default configuration)
			
		when:
			//    5b) MessagingService is created with merged configuration
			TestMessagingService service = builder.build()

		then:
			service.getConfValue() == expected

		where:
			desc																	| afterInitProp 				| beforeBuildServiceProviderProp 					| beforeBuildProp 				| developerCodeValue 	|| expected
			"no developer value and no property value set" 							| null 							| null 												| null 							| null 					|| "before-build service-provider default value"
			"no developer value and after init property set" 						| "value from \${after-init}"	| null 												| null 							| null 					|| "value from \${after-init}"
			"no developer value and service provider property set" 					| null							| "value from \${before-build.service-provider}"	| null 							| null 					|| "value from \${before-build.service-provider}"
			"no developer value and after init and service provider property set" 	| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| null 							| null 					|| "value from \${after-init}"
			
			"no developer value and before build property set"	 					| null 							| null 												| "value from \${before-build}" | null 					|| "value from \${before-build}"
			"no developer value and after init and before build property set" 		| "value from \${after-init}"	| null 												| "value from \${before-build}" | null 					|| "value from \${after-init}"
			"no developer value and service provider and before build property set"	| null							| "value from \${before-build.service-provider}"	| "value from \${before-build}" | null 					|| "value from \${before-build.service-provider}"
			"no developer value and all properties set" 							| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| "value from \${before-build}" | null 					|| "value from \${after-init}"

			"developer value set and no property value set"		 					| null 							| null 												| null 							| "developer value"		|| "developer value"
			"developer value set and after init property set" 						| "value from \${after-init}"	| null 												| null 							| "developer value"		|| "developer value"
			"developer value set and service provider property set" 				| null							| "value from \${before-build.service-provider}"	| null 							| "developer value"		|| "developer value"
			"developer value set and after init and service provider property set" 	| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| null 							| "developer value"		|| "developer value"
			
			"developer value set and before build property set" 					| null 							| null 												| "value from \${before-build}" | "developer value"		|| "developer value"
			"developer value set and after init and before build property set"		| "value from \${after-init}"	| null 												| "value from \${before-build}" | "developer value"		|| "developer value"
			"developer value set and service provider and before build property set"| null							| "value from \${before-build.service-provider}"	| "value from \${before-build}" | "developer value"		|| "developer value"
			"developer value set and all properties set"						 	| "value from \${after-init}"	| "value from \${before-build.service-provider}"	| "value from \${before-build}" | "developer value"		|| "developer value"
	}

	def setSystemProperty(String key, String value) {
		if (value != null) {
			System.setProperty(key, value)
		}
	}
	class TestMessagingService implements MessagingService {
		private final String confValue;
		
		public TestMessagingService(String confValue) {
			super();
			this.confValue = confValue;
		}

		@Override
		public void send(Message message) throws MessagingException {
			// don't care
		}

		public String getConfValue() {
			return confValue;
		}
	}
	class TestMessagingBuilder extends MessagingBuilder {
		private final ConfigurationValueBuilderHelper<?, String> confValueBuiler;
		public TestMessagingBuilder() {
			super(false);
			confValueBuiler = new ConfigurationValueBuilderHelper(this, String.class, buildContext);
		}
		public TestMessagingBuilder confValue(String string) {
			this.confValueBuiler.setValue(string);
			return this;
		}
		public ConfigurationValueBuilder confValue() {
			return confValueBuiler;
		}
		@Override
		public MessagingService build() {
			return new TestMessagingService(confValueBuiler.getValue());
		}
	}
	abstract class AbstractTestConfigurer implements MessagingConfigurer {
		boolean registerDefaultValues;
		Optional<String> registerOptionalValue;
		
		AbstractTestConfigurer(boolean registerDefaultValues, Optional<String> registerOptionalValue) {
			super();
			this.registerDefaultValues = registerDefaultValues
			this.registerOptionalValue = registerOptionalValue
		}
		
		@Override
		void configure(MessagingBuilder builder) {
			configure((TestMessagingBuilder) builder)
		}
		
		abstract void configure(TestMessagingBuilder builder)
	}
	class AfterInitConfigurer extends AbstractTestConfigurer {
		AfterInitConfigurer(boolean registerDefaultValues, Optional<String> registerOptionalValue) {
			super(registerDefaultValues, registerOptionalValue)
		}
		@Override
		void configure(TestMessagingBuilder builder) {
			builder.confValue().properties("\${after-init}")
			if (registerDefaultValues) {
				builder.confValue().defaultValue(overrideIfNotSet("after-init default value"))
			}
			if (registerOptionalValue != null) {
				builder.confValue().value(registerOptionalValue)
			}
		}
	}
	class BeforeBuildServiceProviderConfigurer extends AbstractTestConfigurer {
		BeforeBuildServiceProviderConfigurer(boolean registerDefaultValues, Optional<String> registerOptionalValue) {
			super(registerDefaultValues, registerOptionalValue)
		}
		@Override
		void configure(TestMessagingBuilder builder) {
			builder.confValue().properties("\${before-build.service-provider}")
			if (registerDefaultValues) {
				builder.confValue().defaultValue("before-build service-provider default value")
			}
			if (registerOptionalValue != null) {
				builder.confValue().value(registerOptionalValue)
			}
		}
	}
	class BeforeBuildDefaultConfigurer extends AbstractTestConfigurer {
		BeforeBuildDefaultConfigurer(boolean registerDefaultValues, Optional<String> registerOptionalValue) {
			super(registerDefaultValues, registerOptionalValue)
		}
		@Override
		void configure(TestMessagingBuilder builder) {
			builder.confValue().properties("\${before-build}")
			if (registerDefaultValues) {
				builder.confValue().defaultValue(overrideIfNotSet("before-build default value"))
			}
			if (registerOptionalValue != null) {
				builder.confValue().value(registerOptionalValue)
			}
		}
	}
}
