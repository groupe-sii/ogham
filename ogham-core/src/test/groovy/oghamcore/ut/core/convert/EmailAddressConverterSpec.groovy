package oghamcore.ut.core.convert

import org.apache.commons.beanutils.ConversionException

import fr.sii.ogham.core.util.converter.EmailAddressConverter
import fr.sii.ogham.email.message.EmailAddress
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class EmailAddressConverterSpec extends Specification {
	def "'#source' -> EmailAddress should be #expected"() {
		given:
			def converter = new EmailAddressConverter();
			
		when:
			def result = converter.convert(EmailAddress, source);
			
		then:
			result == expected
			
		where:
			source                        || expected
			""                            || new EmailAddress("")
			"foo@yopmail.com"             || new EmailAddress("foo@yopmail.com")
			"Foo Bar <foo-bar@yop.com>"   || new EmailAddress("foo-bar@yop.com", "Foo Bar")
	}

	def "#source -> EmailAddress can't be converted"() {
		given:
			def converter = new EmailAddressConverter();
			
		when:
			def result = converter.convert(EmailAddress, source);
			
		then:
			def e = thrown(ConversionException)
			e.message == expected
			
			
		where:
			source               || expected
			null                 || "No value specified for 'fr.sii.ogham.email.message.EmailAddress'"
			true                 || "Can't convert value 'true' to type class fr.sii.ogham.email.message.EmailAddress"
			false                || "Can't convert value 'false' to type class fr.sii.ogham.email.message.EmailAddress"
			0                    || "Can't convert value '0' to type class fr.sii.ogham.email.message.EmailAddress"
	}
}
