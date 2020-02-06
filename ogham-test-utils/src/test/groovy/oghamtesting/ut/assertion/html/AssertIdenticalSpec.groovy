package oghamtesting.ut.assertion.html

import static fr.sii.ogham.testing.assertion.html.AssertHtml.assertEquals

import org.junit.ComparisonFailure

import fr.sii.ogham.testing.assertion.html.AssertHtml
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertIdenticalSpec extends Specification {
	def "#desc should be considered as identical"() {
		when:
			assertEquals("<html><head></head><body>${expectedString}</body></html>", "<html><head></head><body>${actualString}</body></html>")
		
		then:
			noExceptionThrown()
		
		where:
			desc							| expectedString						| actualString
			"same string"					| "<div>foo</div>"						| "<div>foo</div>"
			"identical"						| "<p attr1='1' attr2=2>foo</p>"		| "<p attr1=\"1\" attr2=\"2\">foo</p>"
			"different blank characters"	| "<p>\n\r\t foo</p>"					| "<p>foo</p>"
	}
	
	def "#desc should be considered as different"() {
		when:
			assertEquals("<html><head></head><body>${expectedString}</body></html>", "<html><head></head><body>${actualString}</body></html>")
		
		then:
			thrown(ComparisonFailure);
		
		where:
			desc							| expectedString						| actualString
			// FIXME: XMLUnit.compareXML().identical() states that attributes must be in same order
			// when loading documents, it seems that attributes are ordered by name. So different attribute order is not detected using identical
			// "different attribute order"		| "<p attr2='2' attr1='1'>foo</p>"		| "<p attr1='1' attr2='2'>foo</p>"
			"different elements"			| "<span>foo</span>"					| "<div>foo</div>"
	}
}
