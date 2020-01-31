package oghamcore.ut.core.env

import fr.sii.ogham.core.env.FirstExistingPropertiesResolver
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class FirstExistingPropertiesResolverSpec extends Specification {
	def "containsProperty() with #contains1 | #contains2 should #expectedDesc"() {
		given:
			PropertyResolver resolver1 = Mock()
			resolver1.containsProperty(_) >> contains1
			PropertyResolver resolver2 = Mock()
			resolver2.containsProperty(_) >> contains2
			def resolver = new FirstExistingPropertiesResolver(resolver1, resolver2)
		
		when:
			def contains = resolver.containsProperty("foo")
		
		then:
			contains == expected
		
		where:
			contains1 	| contains2 || expected	| expectedDesc
			false		| false		|| false	| "not contain the property"
			false		| true		|| true		| "contain the property"
			true		| false		|| true		| "contain the property"
			true		| true		|| true		| "contain the property"
	}
	
	def "getProperty() with #contains1 | #contains2 should return #expected"() {
		given:
			PropertyResolver resolver1 = Mock()
			resolver1.containsProperty(_) >> contains1
			resolver1.getProperty(_) >> "value1"
			PropertyResolver resolver2 = Mock()
			resolver2.containsProperty(_) >> contains2
			resolver2.getProperty(_) >> "value2"
			def resolver = new FirstExistingPropertiesResolver(resolver1, resolver2)
		
		when:
			def property = resolver.getProperty("foo")
		
		then:
			property == expected
		
		where:
			contains1 	| contains2 || expected
			false		| false		|| null
			false		| true		|| "value2"
			true		| false		|| "value1"
			true		| true		|| "value1"
	}

	def "getProperty(key, #defaultValue) with #contains1 | #contains2 should return #expected"() {
		given:
			PropertyResolver resolver1 = Mock()
			resolver1.containsProperty(_) >> contains1
			resolver1.getProperty(_, _) >> "prop1"
			PropertyResolver resolver2 = Mock()
			resolver2.containsProperty(_) >> contains2
			resolver2.getProperty(_, _) >> "prop2"
			def resolver = new FirstExistingPropertiesResolver(resolver1, resolver2)
		
		when:
			def property = resolver.getProperty("foo", (String) defaultValue)
		
		then:
			property == expected
		
		where:
			contains1	| contains2	| defaultValue	|| expected
			false		| false		| null			|| null
			false		| false		| "bar"			|| "bar"
			false		| true		| null			|| "prop2"
			false		| true		| "bar"			|| "prop2"
			true		| false		| null			|| "prop1"
			true		| false		| "bar"			|| "prop1"
			true		| true		| null			|| "prop1"
			true		| true		| "bar"			|| "prop1"
	}
}
