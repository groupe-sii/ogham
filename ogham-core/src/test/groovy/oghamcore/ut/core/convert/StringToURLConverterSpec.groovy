package oghamcore.ut.core.convert

import fr.sii.ogham.core.convert.StringToURLConverter
import fr.sii.ogham.core.exception.convert.ConversionException
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class StringToURLConverterSpec extends Specification {
	def "#sourceType.getSimpleName() -> URL conversion not supported"() {
		given:
			def converter = new StringToURLConverter();
			
		when:
			def supports = converter.supports(sourceType, URL);
			
		then:
			!supports
			
		where:
			sourceType << [Integer, Boolean, Object]
	}
	
	def "'#source' -> URL should be #expected"() {
		given:
			def converter = new StringToURLConverter();
			
		when:
			def supports = converter.supports(String, URL);
			def result = converter.convert(source, URL);
			
		then:
			supports
			result == expected
			
		where:
			source                        || expected
			// empty string and null
			""                            || null
			null                          || null
			// valid URLs
			"http://foo"                  || new URL("http://foo:80")
			"http://foo:8000"             || new URL("http://foo:8000")
			"http://foo.bar:8000/path"    || new URL("http://foo.bar:8000/path")
	}

	def "'#source' -> URL can't be converted"() {
		given:
			def converter = new StringToURLConverter();
			
		when:
			def supports = converter.supports(String, URL);
			def result = converter.convert(source, URL);
			
		then:
			supports
			def e = thrown(ConversionException)
			e.getCause()!=null
			expected.isAssignableFrom(e.getCause().getClass())
			
			
		where:
			source               || expected
			"foo"                || MalformedURLException
			"//foo"              || MalformedURLException
			"a://foo.bar"        || MalformedURLException
	}
}
