package oghamcore.ut.email

import jakarta.mail.internet.InternetAddress

import fr.sii.ogham.email.message.EmailAddress
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class ParseEmailAddressSpec extends Specification {
	def "#rawAddress should be parsed to address=#expected.address | personal=#expected.personal"() {
		when:
			def address = new EmailAddress(rawAddress)
			def parsedByJava = new InternetAddress(rawAddress)
			
		then:
			address.address == expected.address
			address.personal == expected.personal
			address.address == parsedByJava.address
			address.personal == parsedByJava.personal
			
		where:
			rawAddress                        || expected
			"Foo <foo@bar.com>"               || new EmailAddress("foo@bar.com", "Foo")
			"Foo Bar <foo@bar.com>"           || new EmailAddress("foo@bar.com", "Foo Bar")
			"  __ 123 ABC def ^^ <a@b.c>"     || new EmailAddress("a@b.c", "__ 123 ABC def ^^")
			"'single quoted' <a@b.c>"         || new EmailAddress("a@b.c", "'single quoted'")
			"\"double quoted\" <a@b.c>"       || new EmailAddress("a@b.c", "double quoted")
	}
	
	def "#rawAddress should not be parsed and values #expected"() {
		when:
			def address = new EmailAddress(rawAddress);
			
		then:
			address.address == expected
			address.personal == null
			
		where:
			rawAddress                        || expected
			""                                || ""
			"foo@bar.com"                     || "foo@bar.com"
			"<foo@bar.com>"                   || "<foo@bar.com>"
			"  __ 123 ABC def ^^ <a@b.c> 56"  || "  __ 123 ABC def ^^ <a@b.c> 56"
	}
	
	def "#rawAddress should fail"() {
		when:
			def address = new EmailAddress(rawAddress);
			
		then:
			thrown(expected)
			
		where:
			rawAddress                        || expected
			null                              || IllegalArgumentException
	}
}
