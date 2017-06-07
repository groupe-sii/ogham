package fr.sii.ogham.ut.core.convert

import static spock.lang.Specification.thrown;

import java.util.concurrent.atomic.AtomicInteger

import fr.sii.ogham.core.convert.StringToNumberConverter
import fr.sii.ogham.core.exception.convert.ConversionException
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class StringToNumberConverterSpec extends Specification {
	def "#sourceType.getSimpleName() -> #targetType.getSimpleName() conversion not supported"() {
		given:
			def converter = new StringToNumberConverter();
			
		when:
			def supports = converter.supports(sourceType, targetType);
			
		then:
			!supports
			
		where:
			sourceType           | targetType
			// empty string and null
			Integer              | Integer   
			String               | String    
			String               | Object    
	}
	
	def "'#source' -> #targetType.getSimpleName() should be #expected"() {
		given:
			def converter = new StringToNumberConverter();
			
		when:
			def supports = converter.supports(String, targetType);
			def result = converter.convert(source, targetType);
			
		then:
			supports
			result == expected
			
		where:
			source               | targetType    || expected
			// empty string and null
			""                   | Integer       || null
			""                   | Long          || null
			""                   | Float         || null
			""                   | Double        || null
			""                   | Short         || null
			""                   | Byte          || null
			""                   | BigInteger    || null
			""                   | BigDecimal    || null
			""                   | Number        || null
			null                 | Integer       || null
			null                 | Long          || null
			null                 | Float         || null
			null                 | Double        || null
			null                 | Short         || null
			null                 | Byte          || null
			null                 | BigInteger    || null
			null                 | BigDecimal    || null
			null                 | Number        || null
			// general
			"1"                  | Integer       || Integer.valueOf("1")
			"1"                  | Long          || Long.valueOf("1")
			"1"                  | Float         || Float.valueOf("1")
			"1"                  | Double        || Double.valueOf("1")
			"1"                  | Short         || Short.valueOf("1")
			"1"                  | Byte          || Byte.valueOf("1")
			"1"                  | BigInteger    || new BigInteger("1")
			"1"                  | BigDecimal    || new BigDecimal("1")
			"1"                  | Number        || new BigDecimal("1")
			"-1"                 | Integer       || Integer.valueOf("-1")
			"-1"                 | Long          || Long.valueOf("-1")
			"-1"                 | Float         || Float.valueOf("-1")
			"-1"                 | Double        || Double.valueOf("-1")
			"-1"                 | Short         || Short.valueOf("-1")
			"-1"                 | Byte          || Byte.valueOf("-1")
			"-1"                 | BigInteger    || new BigInteger("-1")
			"-1"                 | BigDecimal    || new BigDecimal("-1")
			"-1"                 | Number        || new BigDecimal("-1")
			"+1"                 | Integer       || Integer.valueOf("1")
			"+1"                 | Long          || Long.valueOf("1")
			"+1"                 | Float         || Float.valueOf("1")
			"+1"                 | Double        || Double.valueOf("1")
			"+1"                 | Short         || Short.valueOf("1")
			"+1"                 | Byte          || Byte.valueOf("1")
			"+1"                 | BigInteger    || new BigInteger("1")
			"+1"                 | BigDecimal    || new BigDecimal("1")
			"+1"                 | Number        || new BigDecimal("1")
			// ignore spaces
			"   1 000  000   "   | Integer       || Integer.valueOf("1000000")
			"   1 000  000   "   | Long          || Long.valueOf("1000000")
			"   1 000  000   "   | Float         || Float.valueOf("1000000")
			"   1 000  000   "   | Double        || Double.valueOf("1000000")
			"   1 0    0     "   | Short         || Short.valueOf("100")
			"   1 0    0     "   | Byte          || Byte.valueOf("100")
			"   1 000  000   "   | BigInteger    || new BigInteger("1000000")
			"   1 000  000   "   | BigDecimal    || new BigDecimal("1000000")
			"   1 000  000   "   | Number        || new BigDecimal("1000000")
			" -  1"              | Integer       || Integer.valueOf("-1")
			" -  1"              | Long          || Long.valueOf("-1")
			" -  1"              | Float         || Float.valueOf("-1")
			" -  1"              | Double        || Double.valueOf("-1")
			" -  1"              | Short         || Short.valueOf("-1")
			" -  1"              | Byte          || Byte.valueOf("-1")
			" -  1"              | BigInteger    || new BigInteger("-1")
			" -  1"              | BigDecimal    || new BigDecimal("-1")
			" -  1"              | Number        || new BigDecimal("-1")
			// floating point
			"1.0"                | Float         || Float.valueOf("1.0")
			"1.0"                | Double        || Double.valueOf("1.0")
			"1.0"                | BigDecimal    || new BigDecimal("1.0")
			"1.0"                | Number        || new BigDecimal("1.0")
			"+1.0"               | Float         || Float.valueOf("1.0")
			"+1.0"               | Double        || Double.valueOf("1.0")
			"+1.0"               | BigDecimal    || new BigDecimal("1.0")
			"+1.0"               | Number        || new BigDecimal("1.0")
			"-1.0"               | Float         || Float.valueOf("-1.0")
			"-1.0"               | Double        || Double.valueOf("-1.0")
			"-1.0"               | BigDecimal    || new BigDecimal("-1.0")
			"-1.0"               | Number        || new BigDecimal("-1.0")
			// combine
			" - 1. 0  "          | Float         || Float.valueOf("-1.0")
			" - 1. 0  "          | Double        || Double.valueOf("-1.0")
			" - 1. 0  "          | BigDecimal    || new BigDecimal("-1.0")
			" - 1. 0  "          | Number        || new BigDecimal("-1.0")
			" + 1. 0  "          | Float         || Float.valueOf("+1.0")
			" + 1. 0  "          | Double        || Double.valueOf("+1.0")
			" + 1. 0  "          | BigDecimal    || new BigDecimal("+1.0")
			" + 1. 0  "          | Number        || new BigDecimal("+1.0")
			// Hexadecimal and Octal
			"0xF"                | Byte          || Byte.valueOf("15")
			"0xF"                | Short         || Short.valueOf("15")
			"0xF"                | Integer       || Integer.valueOf("15")
			"0xF"                | Long          || Long.valueOf("15")
			"0xF"                | BigInteger    || new BigInteger("15")
			"0XF"                | Byte          || Byte.valueOf("15")
			"0XF"                | Short         || Short.valueOf("15")
			"0XF"                | Integer       || Integer.valueOf("15")
			"0XF"                | Long          || Long.valueOf("15")
			"0XF"                | BigInteger    || new BigInteger("15")
			"#F"                 | Byte          || Byte.valueOf("15")
			"#F"                 | Short         || Short.valueOf("15")
			"#F"                 | Integer       || Integer.valueOf("15")
			"#F"                 | Long          || Long.valueOf("15")
			"#F"                 | BigInteger    || new BigInteger("15")
			"0x17"               | Byte          || Byte.valueOf("23")
			"0x17"               | Short         || Short.valueOf("23")
			"0x17"               | Integer       || Integer.valueOf("23")
			"0x17"               | Long          || Long.valueOf("23")
			"0x17"               | BigInteger    || new BigInteger("23")
			"0X17"               | Byte          || Byte.valueOf("23")
			"0X17"               | Short         || Short.valueOf("23")
			"0X17"               | Integer       || Integer.valueOf("23")
			"0X17"               | Long          || Long.valueOf("23")
			"0X17"               | BigInteger    || new BigInteger("23")
			"#17"                | Byte          || Byte.valueOf("23")
			"#17"                | Short         || Short.valueOf("23")
			"#17"                | Integer       || Integer.valueOf("23")
			"#17"                | Long          || Long.valueOf("23")
			"#17"                | BigInteger    || new BigInteger("23")
			"017"                | Byte          || Byte.valueOf("15")
			"017"                | Short         || Short.valueOf("15")
			"017"                | Integer       || Integer.valueOf("15")
			"017"                | Long          || Long.valueOf("15")
			"017"                | BigInteger    || new BigInteger("15")
			" - 0 x F "          | Byte          || Byte.valueOf("-15")
			" - 0 x F "          | Short         || Short.valueOf("-15")
			" - 0 x F "          | Integer       || Integer.valueOf("-15")
			" - 0 x F "          | Long          || Long.valueOf("-15")
			" - 0 x F "          | BigInteger    || new BigInteger("-15")
			" - 0 X F "          | Byte          || Byte.valueOf("-15")
			" - 0 X F "          | Short         || Short.valueOf("-15")
			" - 0 X F "          | Integer       || Integer.valueOf("-15")
			" - 0 X F "          | Long          || Long.valueOf("-15")
			" - 0 X F "          | BigInteger    || new BigInteger("-15")
			" - # F   "          | Byte          || Byte.valueOf("-15")
			" - # F   "          | Short         || Short.valueOf("-15")
			" - # F   "          | Integer       || Integer.valueOf("-15")
			" - # F   "          | Long          || Long.valueOf("-15")
			" - # F   "          | BigInteger    || new BigInteger("-15")
			" - 0 x 71"          | Byte          || Byte.valueOf("-113")
			" - 0 x 71"          | Short         || Short.valueOf("-113")
			" - 0 x 71"          | Integer       || Integer.valueOf("-113")
			" - 0 x 71"          | Long          || Long.valueOf("-113")
			" - 0 x 71"          | BigInteger    || new BigInteger("-113")
			" - 0 X 71"          | Byte          || Byte.valueOf("-113")
			" - 0 X 71"          | Short         || Short.valueOf("-113")
			" - 0 X 71"          | Integer       || Integer.valueOf("-113")
			" - 0 X 71"          | Long          || Long.valueOf("-113")
			" - 0 X 71"          | BigInteger    || new BigInteger("-113")
			" - # 7 1 "          | Byte          || Byte.valueOf("-113")
			" - # 7 1 "          | Short         || Short.valueOf("-113")
			" - # 7 1 "          | Integer       || Integer.valueOf("-113")
			" - # 7 1 "          | Long          || Long.valueOf("-113")
			" - # 7 1 "          | BigInteger    || new BigInteger("-113")
			" - 07 1  "          | Byte          || Byte.valueOf("-57")
			" - 07 1  "          | Short         || Short.valueOf("-57")
			" - 07 1  "          | Integer       || Integer.valueOf("-57")
			" - 07 1  "          | Long          || Long.valueOf("-57")
			" - 07 1  "          | BigInteger    || new BigInteger("-57")
			// octal format but not converted to octal (no sense for floating point numbers)
			"017"                | Float         || Float.valueOf("17.0")
			"017"                | Double        || Double.valueOf("17.0")
			"017"                | BigDecimal    || new BigDecimal("17.0")
			" - 07 1  "          | Float         || Float.valueOf("-71.0")
			" - 07 1  "          | Double        || Double.valueOf("-71.0")
			" - 07 1  "          | BigDecimal    || new BigDecimal("-71.0")
	}

	def "'#source' -> #targetType.getSimpleName() can't be converted"() {
		given:
			def converter = new StringToNumberConverter();
			
		when:
			def supports = converter.supports(String, targetType);
			def result = converter.convert(source, targetType);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getCause()!=null
			expected.isAssignableFrom(e.getCause().getClass())
			
			
		where:
			source               | targetType    || expected
			"1.0"                | Integer       || NumberFormatException
			"1.0"                | Long          || NumberFormatException
			"1.0"                | Short         || NumberFormatException
			"1.0"                | Byte          || NumberFormatException
			"1.0"                | BigInteger    || NumberFormatException
			"1-1"                | Integer       || NumberFormatException
			"1-1"                | Long          || NumberFormatException
			"1-1"                | Short         || NumberFormatException
			"1-1"                | Byte          || NumberFormatException
			"1-1"                | BigInteger    || NumberFormatException
			"0xF"                | Float         || NumberFormatException
			"0xF"                | Double        || NumberFormatException
			"0xF"                | BigDecimal    || NumberFormatException
			"0xF"                | Number        || NumberFormatException
			"#F"                 | Float         || NumberFormatException
			"#F"                 | Double        || NumberFormatException
			"#F"                 | BigDecimal    || NumberFormatException
			"0x17"               | Float         || NumberFormatException
			"0x17"               | Double        || NumberFormatException
			"0x17"               | BigDecimal    || NumberFormatException
			"#17"                | Float         || NumberFormatException
			"#17"                | Double        || NumberFormatException
			"#17"                | BigDecimal    || NumberFormatException
			" - 0 x F "          | Float         || NumberFormatException
			" - 0 x F "          | Double        || NumberFormatException
			" - 0 x F "          | BigDecimal    || NumberFormatException
			" - # F   "          | Float         || NumberFormatException
			" - # F   "          | Double        || NumberFormatException
			" - # F   "          | BigDecimal    || NumberFormatException
			" - 0 x 71"          | Float         || NumberFormatException
			" - 0 x 71"          | Double        || NumberFormatException
			" - 0 x 71"          | BigDecimal    || NumberFormatException
			" - # 7 1 "          | Float         || NumberFormatException
			" - # 7 1 "          | Double        || NumberFormatException
			" - # 7 1 "          | BigDecimal    || NumberFormatException
	}
	

	def "#targetType.getSimpleName() but not supported"() {
		given:
			def converter = new StringToNumberConverter();
			
		when:
			def supports = converter.supports(String, targetType);
			def result = converter.convert(source, targetType);
			
		then:
			supports
			thrown(ConversionException)
			
		where:
			source               | targetType    
			"1.0"                | AtomicInteger 
	}
}
