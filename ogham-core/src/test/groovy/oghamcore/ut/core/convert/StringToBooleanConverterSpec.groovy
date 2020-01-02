package oghamcore.ut.core.convert

import fr.sii.ogham.core.convert.StringToBooleanConverter
import fr.sii.ogham.core.exception.convert.ConversionException
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class StringToBooleanConverterSpec extends Specification {
	def "#sourceType.getSimpleName() -> #targetType.getSimpleName() conversion not supported"() {
		given:
			def converter = new StringToBooleanConverter();
			
		when:
			def supports = converter.supports(sourceType, targetType);
			
		then:
			!supports
			
		where:
			sourceType           | targetType
			Integer              | Integer
			Integer              | String
			Integer              | Object
			String               | Integer    
			String               | String    
			String               | Object    
	}
	
	def "'#source' -> Boolean should be #expected"() {
		given:
			def converter = new StringToBooleanConverter();
			
		when:
			def supports = converter.supports(String, Boolean);
			def result = converter.convert(source, Boolean);
			
		then:
			supports
			result == expected
			
		where:
			source			|| expected
			null			|| null
			""				|| null
			"  "			|| null
			"false"			|| false
			"off"			|| false
			"no"			|| false
			"0"				|| false
			" false"		|| false
			" off"			|| false
			" no"			|| false
			" 0"			|| false
			"false "		|| false
			"off "			|| false
			"no "			|| false
			"0 "			|| false
			" false "		|| false
			" off "			|| false
			" no "			|| false
			" 0 "			|| false
			"FALSE"			|| false
			"OFF"			|| false
			"NO"			|| false

			"true"			|| true
			"on"			|| true
			"yes"			|| true
			"1"				|| true
			" true"			|| true
			" on"			|| true
			" yes"			|| true
			" 1"			|| true
			"true "			|| true
			"on "			|| true
			"yes "			|| true
			"1 "			|| true
			" true "		|| true
			" on "			|| true
			" yes "			|| true
			" 1 "			|| true
			"TRUE"			|| true
			"ON"			|| true
			"YES"			|| true
	}

	def "'#source' -> Boolean can't be converted"() {
		given:
			def converter = new StringToBooleanConverter();
			
		when:
			def supports = converter.supports(String, Boolean);
			def result = converter.convert(source, Boolean);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getMessage() == expected
			
			
		where:
			source					|| expected
			"1.0"					|| "Invalid boolean value '1.0'"
			"YESS"					|| "Invalid boolean value 'YESS'"
			"foo"					|| "Invalid boolean value 'foo'"
	}
	
}
