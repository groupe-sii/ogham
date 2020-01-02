package oghamcore.ut.core.convert

import fr.sii.ogham.core.convert.Converter
import fr.sii.ogham.core.convert.StringToArrayConverter
import fr.sii.ogham.core.convert.SupportingConverter
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class StringToArrayConverterSpec extends Specification {
	def "#sourceType.getSimpleName() -> any[] conversion #supported"() {
		given:
			SupportingConverter elemConverter = Mock()
			elemConverter.supports(_, _) >> elemConverterSupports
			def converter = new StringToArrayConverter(elemConverter);
			
		when:
			def supports = converter.supports(sourceType, Object[]);
			
		then:
			supports == expected
			
		where:
			sourceType     | elemConverterSupports   || expected | supported
			Integer        | false                   || false    | "not supported"
			Boolean        | false                   || false    | "not supported"
			URL            | false                   || false    | "not supported"
			Object         | false                   || false    | "not supported"
			String         | false                   || false    | "not supported"

			Integer        | true                    || false    | "not supported"
			Boolean        | true                    || false    | "not supported"
			URL            | true                    || false    | "not supported"
			Object         | true                    || false    | "not supported"
			String         | true                    || true     | "supported"
	}
	
	def "'#source' -> String[] should be #expected"() {
		given:
			Converter elemConverter = Mock()
			elemConverter.convert(_, _) >> { args -> args[0] }
			def converter = new StringToArrayConverter(elemConverter);
			
		when:
			def supports = converter.supports(String, String[]);
			def result = converter.convert(source, String[]);
			
		then:
			supports
			result == expected as String[]
			
		where:
			source                        || expected
			null                          || null
			""                            || [""]
			"one"                         || ["one"]
			"one,two"                     || ["one", "two"]
			"  one  ,  two  , three "     || ["one", "two", "three"]
	}
	
	def "'#source' -> Person[] should be #expected"() {
		given:
			Converter elemConverter = Mock()
			elemConverter.convert(_, _) >> { args -> new Person(args[0]) }
			def converter = new StringToArrayConverter(elemConverter);
			
		when:
			def supports = converter.supports(String, Person[]);
			def result = converter.convert(source, Person[]);
			
		then:
			supports
			result == expected as Person[]
			
		where:
			source                           || expected
			"john connor"                    || [new Person("john connor")]
			" sarah connor  ,  john connor " || [new Person("sarah connor"), new Person("john connor")]
	}

	class Person {
		String name;
		Person(String name) {
			this.name = name;
		}
		public boolean equals(Object other) {
			return name.equals(other?.name);
		}
		public int hashCode() {
			return name.hashCode();
		}
		public String toString() {
			return name;
		}
	}
}
