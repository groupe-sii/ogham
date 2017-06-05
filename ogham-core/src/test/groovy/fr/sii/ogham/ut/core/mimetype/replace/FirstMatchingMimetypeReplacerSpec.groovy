package fr.sii.ogham.ut.core.mimetype.replace;

import fr.sii.ogham.core.mimetype.replace.FirstMatchingMimetypeReplacer
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class FirstMatchningMimetypeReplacerSpec extends Specification {

	def "#name applied on #mimetype should be #expected"() {
		given:
			MimetypeReplacer delegate1 = Mock()
			MimetypeReplacer delegate2 = Mock()
			delegate1.replace(_) >> { String m -> given1(m) }
			delegate2.replace(_) >> { String m -> given2(m) }

		when:
			def result = new FirstMatchingMimetypeReplacer(Arrays.asList(delegate1, delegate2)).replace(mimetype)

		then:
			result.equals(expected)

		where:
			name                 | mimetype | given1              | given2               || expected
			"no replacement"     | "foo"    | { m -> m }          | { m -> m }           || "foo"
			"first replacement"  | "foo"    | { m -> "bar"}       | { m -> m }           || "bar"
			"second replacement" | "foo"    | { m -> m }          | { m -> "foobar" }    || "foobar"
			"both replacements"  | "foo"    | { m -> "bar" }      | { m -> "foobar" }    || "bar"
	}
}
