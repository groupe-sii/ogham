package oghamcore.ut.core.convert

import java.lang.reflect.InvocationTargetException

import fr.sii.ogham.core.convert.StringToEnumConverter
import fr.sii.ogham.core.convert.StringToEnumConverter.FactoryMethod
import fr.sii.ogham.core.exception.convert.ConversionException
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class StringToEnumConverterSpec extends Specification {
	def "#sourceType.getSimpleName() -> Enum conversion not supported"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(sourceType, SimpleEnum);
			
		then:
			!supports
			
		where:
			sourceType << [Integer, Boolean, URL, Object]
	}
	
	def "'#source' -> Enum should be #expected"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(String, SimpleEnum);
			def result = converter.convert(source, SimpleEnum);
			
		then:
			supports
			result == expected
			
		where:
			source                        || expected
			// empty string and null
			""                            || null
			null                          || null
			// valid enum names
			"VALUE_1"                     || SimpleEnum.VALUE_1
			"VALUE_2"                     || SimpleEnum.VALUE_2
	}

	def "'#source' -> Enum can't be converted"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(String, SimpleEnum);
			def result = converter.convert(source, SimpleEnum);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getCause()!=null
			expected.isAssignableFrom(e.getCause().getClass())
			
			
		where:
			source               || expected
			"foo"                || IllegalArgumentException
	}
	
	def "#sourceType.getSimpleName() -> Enum with custom factory method conversion not supported"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(sourceType, EnumWithCustomFactoryMethod);
			
		then:
			!supports
			
		where:
			sourceType << [Integer, Boolean, URL, Object]
	}
	
	def "'#source' -> Enum with custom factory method should be #expected"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(String, EnumWithCustomFactoryMethod);
			def result = converter.convert(source, EnumWithCustomFactoryMethod);
			
		then:
			supports
			result == expected
			
		where:
			source                        || expected
			// empty string and null
			""                            || null
			null                          || null
			// valid enum values
			"1"                           || EnumWithCustomFactoryMethod.VALUE_1
			"2"                           || EnumWithCustomFactoryMethod.VALUE_2
	}
	
	def "'#source' -> Enum with custom factory method can't be converted"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(String, EnumWithCustomFactoryMethod);
			def result = converter.convert(source, EnumWithCustomFactoryMethod);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getCause()!=null
			expected.isAssignableFrom(e.getCause().getClass())
			
			
		where:
			source               || expected
			"foo"                || InvocationTargetException
	}
	
	def "'#source' -> Enum with invalid factory method can't be converted"() {
		given:
			def converter = new StringToEnumConverter();
			
		when:
			def supports = converter.supports(String, EnumWithInvalidFactoryMethod);
			def result = converter.convert(source, EnumWithInvalidFactoryMethod);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getCause()!=null
			expected.isAssignableFrom(e.getCause().getClass())
			
			
		where:
			source               || expected
			"foo"                || NoSuchMethodException
	}

	
	static enum SimpleEnum {
		VALUE_1,
		VALUE_2;
	}
	
	@FactoryMethod(name="of")
	static enum EnumWithCustomFactoryMethod {
		VALUE_1("1"),
		VALUE_2("2");
		
		private final String value;
		EnumWithCustomFactoryMethod(String value) {
			this.value = value;
		}
		
		public static EnumWithCustomFactoryMethod of(String value) {
			for (EnumWithCustomFactoryMethod e : values()) {
				if (e.value.equals(value)) {
					return e;
				}
			}
			throw new IllegalArgumentException("unknown: "+value);
		}
	}
	
	@FactoryMethod(name="of")
	static enum EnumWithInvalidFactoryMethod {
	}
}
