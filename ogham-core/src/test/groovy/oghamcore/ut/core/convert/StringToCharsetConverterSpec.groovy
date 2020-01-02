package oghamcore.ut.core.convert

import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

import fr.sii.ogham.core.convert.StringToCharsetConverter
import fr.sii.ogham.core.exception.convert.ConversionException
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class StringToCharsetConverterSpec extends Specification {
	def "#sourceType.getSimpleName() -> Charset conversion not supported"() {
		given:
			def converter = new StringToCharsetConverter();
			
		when:
			def supports = converter.supports(sourceType, Charset);
			
		then:
			!supports
			
		where:
			sourceType << [Integer, Boolean, URL, Object]
	}
	
	def "'#source' -> Charset should be #expected"() {
		given:
			def converter = new StringToCharsetConverter();
			
		when:
			def supports = converter.supports(String, Charset);
			def result = converter.convert(source, Charset);
			
		then:
			supports
			result == expected
			
		where:
			source                        || expected
			// empty string and null
			""                            || null
			null                          || null
			// valid Charsets
			"UTF-8"                       || Charset.forName("UTF-8")
			"UTF-16"                      || Charset.forName("UTF-16")
			"ISO-8859-1"                  || Charset.forName("ISO-8859-1")
	}

	def "'#source' -> Charset can't be converted"() {
		given:
			def converter = new StringToCharsetConverter();
			
		when:
			def supports = converter.supports(String, Charset);
			def result = converter.convert(source, Charset);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getCause()!=null
			expected.isAssignableFrom(e.getCause().getClass())
			
			
		where:
			source               || expected
			"foo"                || UnsupportedCharsetException
	}
}
