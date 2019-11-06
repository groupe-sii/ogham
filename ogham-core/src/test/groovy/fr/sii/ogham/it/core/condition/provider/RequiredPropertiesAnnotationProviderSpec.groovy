package fr.sii.ogham.it.core.condition.provider

import org.junit.Rule

import fr.sii.ogham.core.builder.annotation.RequiredProperties
import fr.sii.ogham.core.builder.annotation.RequiredProperty
import fr.sii.ogham.core.condition.provider.RequiredPropertiesAnnotationProvider
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.junit.LoggingTestRule
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RequiredPropertiesAnnotationProviderSpec extends Specification {
	@Rule
	LoggingTestRule logging;

	def "no annotation should always return true"() {
		given:
			PropertyResolver resolver = Mock()
			String obj = "abc"
			
		when:
			def condition = new RequiredPropertiesAnnotationProvider(resolver).provide(null)
			def result = condition.accept(obj)

		then:
			result == true
	}
	
	def "@RequiredProperty() should always return true"() {
		given:
			PropertyResolver resolver = Mock()
			RequiredProperties annotation = Mock()
			annotation.value() >> []
			annotation.props() >> []
			String obj = "abc"
			
		when:
			def condition = new RequiredPropertiesAnnotationProvider(resolver).provide(annotation)
			def result = condition.accept(obj)

		then:
			result == true
	}
	
	def "@RequiredProperties(#property1, #property2) - #hasProperty1 and #hasProperty2 should be #expected"() {
		given:
			PropertyResolver resolver = Mock()
			resolver.containsProperty(_) >>> [hasProperty1, hasProperty2]
			RequiredProperties annotation = Mock()
			annotation.value() >>> [property1, property2]
			annotation.props() >> []
			String obj = "abc"
		
		when:
			def condition = new RequiredPropertiesAnnotationProvider(resolver).provide(annotation)
			def result = condition.accept(obj)
			
		then:
			result == expected
			
		where:
			property1 | property2 | hasProperty1 | hasProperty2 || expected
			"foo.bar" | "foo.baz" | false        | false        || false
			"foo.bar" | "foo.baz" | false        | true         || false
			"foo.bar" | "foo.baz" | true         | false        || false
			"foo.bar" | "foo.baz" | true         | true         || true
	}

	def "@RequiredProperties(@props=null) should always return true"() {
		given:
			PropertyResolver resolver = Mock()
			RequiredProperties annotation = Mock()
			annotation.value() >> []
			annotation.props() >> [null]
			String obj = "abc"
		
		when:
			def condition = new RequiredPropertiesAnnotationProvider(resolver).provide(annotation)
			def result = condition.accept(obj)
			
		then:
			result == true
	}
	
	def "@RequiredProperties(@RequiredProperty(is=#matchValue pattern=#matchPattern excludes=#excludes alt=#alternatives)) - {#propertyValue}{#altPropertyValue} should be #expected"() {
		given:
			PropertyResolver resolver = Mock()
			resolver.containsProperty("key") >> { String k -> propertyValue!=null }
			resolver.containsProperty("alt") >> { String k -> altPropertyValue!=null }
			resolver.getProperty("key") >> propertyValue
			resolver.getProperty("alt") >> altPropertyValue
			RequiredProperty requiredProp = Mock()
			requiredProp.value() >> "key"
			requiredProp.is() >> matchValue
			requiredProp.pattern() >> matchPattern
			requiredProp.flags() >> 0
			requiredProp.excludes() >> excludes
			requiredProp.alternatives() >> alternatives
			RequiredProperties annotation = Mock()
			annotation.value() >> []
			annotation.props() >> [requiredProp]
			String obj = "abc"
		
		when:
			def condition = new RequiredPropertiesAnnotationProvider(resolver).provide(annotation)
			def result = condition.accept(obj)
			
		then:
			result == expected
			
		where:
			propertyValue | matchValue | matchPattern | excludes | alternatives | altPropertyValue || expected
			// value for primary key
			"val"         | ""         | ""           | []       | []           | null             || true
			"val"         | ""         | ""           | []       | ["alt"]      | null             || true
			"val"         | ""         | ""           | []       | ["alt"]      | "val"            || true
			"val"         | "val"      | ""           | []       | []           | null             || true
			"val"         | "val"      | ""           | []       | ["alt"]      | null             || true
			"val"         | "val"      | ""           | []       | ["alt"]      | "val"            || true
			"val"         | ""         | "v.*"        | []       | []           | null             || true
			"val"         | ""         | "v.*"        | []       | ["alt"]      | null             || true
			"val"         | ""         | "v.*"        | []       | ["alt"]      | "val"            || true
			"val"         | ""         | ""           | ["val"]  | []           | null             || false
			"val"         | ""         | ""           | ["val"]  | ["alt"]      | null             || false
			"val"         | ""         | ""           | ["val"]  | ["alt"]      | "val"            || false
			// no value for primary key
			null          | ""         | ""           | []       | []           | null             || false
			null          | ""         | ""           | []       | ["alt"]      | null             || false
			null          | ""         | ""           | []       | ["alt"]      | "val"            || true
			null          | "val"      | ""           | []       | []           | null             || false
			null          | "val"      | ""           | []       | ["alt"]      | null             || false
			null          | "val"      | ""           | []       | ["alt"]      | "val"            || true
			null          | ""         | "v.*"        | []       | []           | null             || false
			null          | ""         | "v.*"        | []       | ["alt"]      | null             || false
			null          | ""         | "v.*"        | []       | ["alt"]      | "val"            || true
			null          | ""         | ""           | ["val"]  | []           | null             || false
			null          | ""         | ""           | ["val"]  | ["alt"]      | null             || false
			null          | ""         | ""           | ["val"]  | ["alt"]      | "val"            || false
	}
}
