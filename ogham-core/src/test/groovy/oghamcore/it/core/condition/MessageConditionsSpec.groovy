package oghamcore.it.core.condition

import java.util.regex.Pattern

import fr.sii.ogham.core.condition.Condition
import fr.sii.ogham.core.condition.fluent.MessageConditions
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.core.message.Message
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class MessageConditionsSpec extends Specification {
	def "MessageConditions.and(#accepted1 #accepted2) == #expected"() {
		given:
			Condition<Message> condition1 = Mock();
			Condition<Message> condition2 = Mock();
			condition1.accept(_) >> accepted1
			condition2.accept(_) >> accepted2
			
		when:
			def result1 = MessageConditions.and(condition1, condition2).accept(null);
			def result2 = MessageConditions.and(Arrays.asList(condition1, condition2)).accept(null);
			
		then:
			result1 == expected
			result2 == expected
			
		where:
			accepted1 | accepted2 || expected
			false     | false     || false
			false     | true      || false
			true      | false     || false
			true      | true      || true
	}
	
	def "MessageConditions.or(#accepted1 #accepted2) == #expected"() {
		given:
			Condition<Message> condition1 = Mock();
			Condition<Message> condition2 = Mock();
			condition1.accept(_) >> accepted1
			condition2.accept(_) >> accepted2
			
		when:
			def result1 = MessageConditions.or(condition1, condition2).accept(null);
			def result2 = MessageConditions.or(Arrays.asList(condition1, condition2)).accept(null);
			
		then:
			result1 == expected
			result2 == expected
			
		where:
			accepted1 | accepted2 || expected
			false     | false     || false
			false     | true      || true
			true      | false     || true
			true      | true      || true
	}
	
	def "MessageConditions.not(#accepted) == #expected"() {
		given:
			Condition<Message> condition = Mock();
			condition.accept(_) >> accepted
			
		when:
			def result = MessageConditions.not(condition).accept(null);
			
		then:
			result == expected
		
		where:
			accepted || expected
			false    || true
			true     || false
	}

	def "MessageConditions.alwaysTrue() == #expected"() {
		when:
			def result = MessageConditions.alwaysTrue().accept(obj);
			
		then:
			result == expected
		
		where:
			obj   || expected
			1     || true
			null  || true
			"foo" || true
	}

	def "MessageConditions.alwaysFalse() == #expected"() {
		when:
			def result = MessageConditions.alwaysFalse().accept(obj);
			
		then:
			result == expected
		
		where:
			obj   || expected
			1     || false
			null  || false
			"foo" || false
	}

	def "MessageConditions.requiredProperty(foo) with #existing foo == #expected"() {
		given:
			PropertyResolver resolver = Mock();
			resolver.containsProperty(_) >> exists
			
		when:
			def result = MessageConditions.requiredProperty(resolver, "foo").accept(null);
			
		then:
			result == expected
		
		where:
			existing       | exists || expected
			"not existing" | false  || false
			"existing"     | true   || true
	}
	
	def "MessageConditions.requiredPropertyValue(foo value=#matchValue) with foo=#propertyValue == #expected"() {
		given:
			PropertyResolver resolver = Mock();
			resolver.containsProperty(_) >> { String k -> propertyValue!=null }
			resolver.getProperty(_) >> propertyValue
			
		when:
			def result = MessageConditions.requiredPropertyValue(resolver, "foo", (String) matchValue).accept(null);
			
		then:
			result == expected
		
		where:
			matchValue     | propertyValue    || expected
			"---"          | null             || false
			"---"          | "val"            || false
			"val"          | null             || false
			"val"          | "val"            || true
			"v"            | "val"            || false
			null           | "val"            || false
	}
	
	def "MessageConditions.requiredPropertyValue(foo pattern=#matchPattern) with foo=#propertyValue == #expected"() {
		given:
			PropertyResolver resolver = Mock();
			resolver.containsProperty(_) >> { String k -> propertyValue!=null }
			resolver.getProperty(_) >> propertyValue
			
		when:
			def result = MessageConditions.requiredPropertyValue(resolver, "foo", (Pattern) matchPattern).accept(null);
			
		then:
			result == expected
		
		where:
			matchPattern           | propertyValue    || expected
			Pattern.compile("---") | null             || false
			Pattern.compile("---") | "val"            || false
			Pattern.compile("v.*") | null             || false
			Pattern.compile("v.*") | "val"            || true
			Pattern.compile("v")   | "val"            || false
			null                   | "val"            || false
	}
}
