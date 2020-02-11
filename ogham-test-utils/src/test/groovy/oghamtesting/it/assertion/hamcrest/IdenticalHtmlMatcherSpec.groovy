package oghamtesting.it.assertion.hamcrest

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml
import static org.hamcrest.MatcherAssert.assertThat

import java.util.function.Consumer

import fr.sii.ogham.testing.assertion.hamcrest.IdenticalHtmlMatcher
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class IdenticalHtmlMatcherSpec extends Specification {
	def "#desc"() {
		when:
			def exception
			try {
				assertThat(actualHtml, isIdenticalHtml(expectedHtml))
			} catch(Throwable e) {
				exception = [klass: e.getClass(), message: oneline(e.message)]
			}
			
		then:
			exception == expected
			
		where:
			desc												| actualHtml							| expectedHtml								|| expected
			"actual is null should not throw NPE"				| null									| "foo"										|| 
						[klass: AssertionError, message: ' Expected: "foo" but: was null']
			"expected is null should indicate invalid value"	| "foo"									| null										|| 
						[klass: IllegalArgumentException, message: "expected html can't be null"]
			"not html should pass"								| "foo"									| "foo"										|| null
			"same html should pass"								| wrapHtml("<div a=1 b=2></div>")		| wrapHtml("<div a=1 b=2></div>")			|| null
			"different attribute order should pass"				| wrapHtml("<div a=1 b=2></div>")		| wrapHtml("<div b='2' a='1'></div>")		|| null
			"different elements order should fail"				| wrapHtml("<div></div><p></p>")		| wrapHtml("<p></p><div></div>")			|| 
						[klass: AssertionError, message: ' Expected: "<html><head></head><body><p></p><div></div></body></html>" but: was "<html><head></head><body><div></div><p></p></body></html>"']
			"different html structure should fail"				| wrapHtml("<div></div><span></span>")	| wrapHtml("<p></p><span></span>")			|| 
						[klass: AssertionError, message: ' Expected: "<html><head></head><body><p></p><span></span></body></html>" but: was "<html><head></head><body><div></div><span></span></body></html>"']
			"different attributes should fail"					| wrapHtml("<div a=1 b=2></div>")		| wrapHtml("<div a=1 b=2 c=3></div>")		|| 
						[klass: AssertionError, message: ' Expected: "<html><head></head><body><div a=1 b=2 c=3></div></body></html>" but: was "<html><head></head><body><div a=1 b=2></div></body></html>"']
	}
	
	def "#desc should print differences"() {
		given:
			Consumer printer = Mock()
			def matcher = new IdenticalHtmlMatcher(expectedHtml, printer)
			
		when:
			matcher.matches(actualHtml)
			
		then:
			1 * printer.accept({ diff -> expected.every { return diff.contains(it) }})
			
		where:
			desc												| actualHtml							| expectedHtml								|| expected
			"different elements order should fail"				| wrapHtml("<div></div><p></p>")		| wrapHtml("<p></p><div></div>")			|| [
						 "- Expected sequence of child nodes \'0\' but was \'1\'",
						 "- Expected sequence of child nodes \'1\' but was \'0\'",
						]
			"different html structure should fail"				| wrapHtml("<div></div><span></span>")	| wrapHtml("<p></p><p></p>")				|| [
						 "- Expected element tag name \'p\' but was \'div\'",
						 "- Expected element tag name \'p\' but was \'span\'",
						]
			"different attributes should fail"					| wrapHtml("<div a=1 b=2></div>")		| wrapHtml("<div a=1 b=2 c=3></div>")		|| [
						 "- Expected number of element attributes \'3\' but was \'2\'",
						 "- Expected attribute name \'c\' but was \'null\'"
						]
	}
	
	
	private String oneline(String message) {
		if (message == null) {
			return null
		}
		return message.replaceAll("\\s+", " ")
	}
	
	private String wrapHtml(String str) {
		return "<html><head></head><body>${str}</body></html>"
	}
}
