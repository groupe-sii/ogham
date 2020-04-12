package oghamsmsglobal.it

import static fr.sii.ogham.sms.builder.cloudhopper.InterfaceVersion.VERSION_3_4
import static fr.sii.ogham.testing.assertion.OghamInternalAssertions.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.not

import fr.sii.ogham.core.builder.MessagingBuilder
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification

@LogTestInformation
class SmsglobalServiceProviderConfigurerSpec extends Specification {
	
	def "SmsGlobal service provider configuration should use TLV message_payload and disable splitter"() {
		given:
			MessagingBuilder builder = MessagingBuilder.standard()
			builder
				.environment()
					.properties()
						.set("ogham.sms.smpp.host", "smsglobal.com")

		when:
			def service = builder.build()
			
		then:
			assertThat(service)
				.cloudhopper()
					.host(is("smsglobal.com"))
					.port(is(1775))
					.interfaceVersion(is(VERSION_3_4.value()))
					.userData()
						.useShortMessage(is(false))
						.useMessagePayloadTlvParameter(is(true))
						.and()
					.splitter()
						.enabled(is(false))
	}
	
	def "SmsGlobal service provider forced configuration should use TLV message_payload and disable splitter"() {
		given:
			MessagingBuilder builder = MessagingBuilder.standard()
			builder
				.environment()
					.properties()
						.set("ogham.sms.smpp.host", "localhost")
						.set("ogham.sms.smsglobal.service-provider.auto-conf.force", true)

		when:
			def service = builder.build()
			
		then:
			assertThat(service)
				.cloudhopper()
					.host(is("localhost"))
					.port(is(1775))
					.interfaceVersion(is(VERSION_3_4.value()))
					.userData()
						.useShortMessage(is(false))
						.useMessagePayloadTlvParameter(is(true))
						.and()
					.splitter()
						.enabled(is(false))
	}
	
	def "SmsGlobal service provider configuration should be overridable by developer"() {
		given:
			MessagingBuilder builder = MessagingBuilder.standard()
			builder
				.environment()
					.properties()
						.set("ogham.sms.smpp.host", "smsglobal.com")
						.and()
					.and()
				.sms()
					.sender(CloudhopperBuilder.class)
						.port(5000)
						.userData()
							.useShortMessage(true)
							.useTlvMessagePayload(false)
							.and()
						.splitter()
							.enable(true)

		when:
			def service = builder.build()
			
		then:
			assertThat(service)
				.cloudhopper()
					.host(is("smsglobal.com"))
					.port(is(5000))
					.userData()
						.useShortMessage(is(true))
						.useMessagePayloadTlvParameter(is(false))
						.and()
					.splitter()
						.enabled(is(true))
	}

	def "SmsGlobal service provider configuration should not be used if host is not smsglobal.com"() {
		given:
			MessagingBuilder builder = MessagingBuilder.standard()
			builder
				.environment()
					.properties()
						.set("ogham.sms.smpp.host", "localhost")

		when:
			def service = builder.build()
			
		then:
			assertThat(service)
				.cloudhopper()
					.host(is("localhost"))
					.port(is(not(1775)))
					.userData()
						.useShortMessage(is(true))
						.useMessagePayloadTlvParameter(is(false))
						.and()
					.splitter()
						.enabled(is(true))
	}
}
