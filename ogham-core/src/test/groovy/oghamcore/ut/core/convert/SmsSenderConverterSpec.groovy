package oghamcore.ut.core.convert

import org.apache.commons.beanutils.ConversionException

import fr.sii.ogham.core.util.converter.SmsSenderConverter
import fr.sii.ogham.sms.message.Sender
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class SmsSenderConverterSpec extends Specification {
	def "'#source' -> Sender should be #expected"() {
		given:
			def converter = new SmsSenderConverter();
			
		when:
			def result = converter.convert(Sender, source);
			
		then:
			result == expected
			
		where:
			source                        || expected
			""                            || new Sender("")
			"060102030405"                || new Sender("060102030405")
			"+3312345678910"              || new Sender("+3312345678910")
	}

	def "#source -> Sender can't be converted"() {
		given:
			def converter = new SmsSenderConverter();
			
		when:
			def result = converter.convert(Sender, source);
			
		then:
			def e = thrown(ConversionException)
			e.message == expected
			
			
		where:
			source               || expected
			null                 || "No value specified for 'fr.sii.ogham.sms.message.Sender'"
			true                 || "Can't convert value 'true' to type class fr.sii.ogham.sms.message.Sender"
			false                || "Can't convert value 'false' to type class fr.sii.ogham.sms.message.Sender"
			0                    || "Can't convert value '0' to type class fr.sii.ogham.sms.message.Sender"
	}
}
