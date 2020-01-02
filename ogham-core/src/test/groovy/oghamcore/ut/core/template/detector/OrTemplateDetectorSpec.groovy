package oghamcore.ut.core.template.detector

import fr.sii.ogham.core.resource.path.UnresolvedPath
import fr.sii.ogham.core.template.detector.OrTemplateDetector
import fr.sii.ogham.core.template.detector.TemplateEngineDetector
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class OrTemplateDetectorSpec extends Specification {
	def "#given1 or #given2 should return #expected"() {
		given:
			TemplateEngineDetector detector1 = Mock()
			TemplateEngineDetector detector2 = Mock()
			detector1.canParse(_, _) >> given1
			detector2.canParse(_, _) >> given2

		when:
			def result = new OrTemplateDetector(Arrays.asList(detector1, detector2)).canParse(new UnresolvedPath("foo"), null)

		then:
			result.equals(expected)

		where:
			given1 | given2 || expected
			false  | false  || false
			false  | true   || true
			true   | false  || true
			true   | true   || true
	}

}
