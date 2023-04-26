package oghamtesting.ut.assertion.html

import static fr.sii.ogham.testing.assertion.html.AssertHtml.assertSimilar

import org.opentest4j.AssertionFailedError

import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class AssertSimilarSpec extends Specification {
	def "#desc should be considered as similar"() {
		when:
			assertSimilar("<html><head></head><body>${expectedString}</body></html>", "<html><head></head><body>${actualString}</body></html>")
		
		then:
			noExceptionThrown()
		
		where:
			desc							| expectedString						| actualString
			"same string"					| "<div>foo</div>"						| "<div>foo</div>"
			"identical"						| "<p attr1='1' attr2=2>foo</p>"		| "<p attr1=\"1\" attr2=\"2\">foo</p>"
			"different blank characters"	| "<p>\n\r\t foo</p>"					| "<p>foo</p>"
			"different attribute order"		| "<p attr2='2' attr1='1'>foo</p>"		| "<p attr1='1' attr2='2'>foo</p>"
	}
	
	def "#desc should be considered as different"() {
		when:
			assertSimilar("<html><head></head><body>${expectedString}</body></html>", "<html><head></head><body>${actualString}</body></html>")
		
		then:
			thrown(AssertionFailedError);
		
		where:
			desc							| expectedString						| actualString
			"different elements"			| "<span>foo</span>"					| "<div>foo</div>"
	}
}
