package oghamcore.ut.core.mimetype.validation

import fr.sii.ogham.core.mimetype.MimeType

import fr.sii.ogham.core.mimetype.validation.MatchMimetypePredicate
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class MatchMimetypePredicateSpec extends Specification {
	def "#matcher should #desc #mimetype"() {
		given:
			def predicate = new MatchMimetypePredicate(matcher)
			def mime = Mock(MimeType)
			mime.toString() >> mimetype
			
		when:
			def matches = predicate.test(mime)
			
		then:
			matches == expected
			
		where:
			matcher					| mimetype						|| expected
			"text/html"				| "text/plain"					|| false
			"application/plain"		| "text/plain"					|| false
			"*/html"				| "text/plain"					|| false
			"text/plain"			| "text/plain"					|| true
			"text/*"				| "text/plain"					|| true
			"*/plain"				| "text/plain"					|| true
			"*/*"					| "text/plain"					|| true
			"*"						| "text/plain"					|| true
			"t*t/p*in"				| "text/plain"					|| true
			// with parameters
			"text/html"				| "text/plain;charset=plain"	|| false
			"application/plain"		| "text/plain;charset=plain"	|| false
			"*/html"				| "text/plain;charset=plain"	|| false
			"text/plain"			| "text/plain;charset=plain"	|| true
			"text/*"				| "text/plain;charset=plain"	|| true
			"*/plain"				| "text/plain;charset=plain"	|| true
			"*/*"					| "text/plain;charset=plain"	|| true
			"*"						| "text/plain;charset=plain"	|| true
			"text/plain"			| "text/plain;charset=plain"	|| true
			"t*t/p*in"				| "text/plain;charset=plain"	|| true
			
			desc = expected ? "match" : "not match"
	}
}
