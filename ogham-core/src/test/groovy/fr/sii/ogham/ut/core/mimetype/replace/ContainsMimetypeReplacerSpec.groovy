package fr.sii.ogham.ut.core.mimetype.replace;

import fr.sii.ogham.core.mimetype.replace.ContainsMimetypeReplacer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ContainsMimetypeReplacerSpec extends Specification {
	def "replace #contains (ignoring case) by #replacement contains rule applied on #mimetype should be #expected"() {
		expect:
			new ContainsMimetypeReplacer(contains, true, replacement).replace(mimetype).equals(expected)

		where:
			contains                | replacement | mimetype                              || expected
			"application/xhtml+xml" | "text/html" | "foo"                                 || "foo"
			"application/xhtml+xml" | "text/html" | "application/xhtml+xml"               || "text/html"
			"application/xhtml+xml" | "text/html" | "application/xhtml+xml;charset=UTF-8" || "text/html"
			"application/xhtml"     | "text/html" | "foo"                                 || "foo"
			"application/xhtml"     | "text/html" | "application/xhtml+xml"               || "text/html"
			"application/xhtml"     | "text/html" | "application/xhtml+xml;charset=UTF-8" || "text/html"
			"application/xhtml+xml" | "text/html" | "foo"                                 || "foo"
			"application/xhtml+xml" | "text/html" | "application/XHTML+xml"               || "text/html"
			"application/xhtml+xml" | "text/html" | "application/XHTML+xml;charset=UTF-8" || "text/html"
			"application/xhtml"     | "text/html" | "foo"                                 || "foo"
			"application/xhtml"     | "text/html" | "application/XHTML+xml"               || "text/html"
			"application/xhtml"     | "text/html" | "application/XHTML+xml;charset=UTF-8" || "text/html"
	}

	def "replace #contains (case sensitive) by #replacement contains rule applied on #mimetype should be #expected"() {
		expect:
			new ContainsMimetypeReplacer(contains, false, replacement).replace(mimetype).equals(expected)

		where:
			contains                | replacement | mimetype                              || expected
			"application/xhtml+xml" | "text/html" | "foo"                                 || "foo"
			"application/xhtml+xml" | "text/html" | "application/xhtml+xml"               || "text/html"
			"application/xhtml+xml" | "text/html" | "application/xhtml+xml;charset=UTF-8" || "text/html"
			"application/xhtml"     | "text/html" | "foo"                                 || "foo"
			"application/xhtml"     | "text/html" | "application/xhtml+xml"               || "text/html"
			"application/xhtml"     | "text/html" | "application/xhtml+xml;charset=UTF-8" || "text/html"
			"application/xhtml+xml" | "text/html" | "foo"                                 || "foo"
			"application/xhtml+xml" | "text/html" | "application/XHTML+xml"               || "application/XHTML+xml"
			"application/xhtml+xml" | "text/html" | "application/XHTML+xml;charset=UTF-8" || "application/XHTML+xml;charset=UTF-8"
			"application/xhtml"     | "text/html" | "foo"                                 || "foo"
			"application/xhtml"     | "text/html" | "application/XHTML+xml"               || "application/XHTML+xml"
			"application/xhtml"     | "text/html" | "application/XHTML+xml;charset=UTF-8" || "application/XHTML+xml;charset=UTF-8"
	}
}
