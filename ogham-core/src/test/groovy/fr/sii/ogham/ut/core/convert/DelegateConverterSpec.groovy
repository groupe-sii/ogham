package fr.sii.ogham.ut.core.convert

import static spock.lang.Specification.*

import org.junit.Rule

import fr.sii.ogham.core.convert.DelegateConverter
import fr.sii.ogham.core.convert.SupportingConverter
import fr.sii.ogham.core.exception.convert.ConversionException
import fr.sii.ogham.helper.rule.LoggingTestRule
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DelegateConverterSpec extends Specification {
	@Rule
	LoggingTestRule logging;
	
	def "#source converted to #targetType should be #expected"() {
		given:
			SupportingConverter converter1 = Mock()
			SupportingConverter converter2 = Mock()
			converter1.convert(_, _) >> convert1
			converter1.supports(_, _) >> supports1
			converter2.convert(_, _) >> convert2
			converter2.supports(_, _) >> supports2

		when:
			def result = new DelegateConverter(converter1, converter2).convert(source, targetType)

		then:
			result.equals(expected)

		where:
			source   | targetType    | supports1   | convert1 | supports2  | convert2   || expected
			"string" | Integer.class | true        | 1        | false      | null       || 1
			"string" | Integer.class | false       | null     | true       | 2          || 2
			null     | Integer.class | true        | 4        | true       | 2          || null
	}

	
	def "no converter supporting conversion should fail"() {
		given:
			SupportingConverter converter1 = Mock()
			SupportingConverter converter2 = Mock()
			converter1.convert(_, _) >> convert1
			converter1.supports(_, _) >> supports1
			converter2.convert(_, _) >> convert2
			converter2.supports(_, _) >> supports2

		when:
			new DelegateConverter(converter1, converter2).convert(source, targetType)
		
		then:
			thrown(ConversionException)

		where:
			source   | targetType    | supports1   | convert1 | supports2  | convert2
			"string" | Integer.class | false       | null     | false      | null
	}
}
