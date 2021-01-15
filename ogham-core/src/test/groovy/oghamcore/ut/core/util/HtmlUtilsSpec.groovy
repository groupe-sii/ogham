package oghamcore.ut.core.util

import fr.sii.ogham.core.util.CssUrlFunction
import fr.sii.ogham.core.util.HtmlUtils
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class HtmlUtilsSpec extends Specification {
	def "getCssUrlFunctions(#cssValue) should extract url #expected.url"() {
		when:
			def results = HtmlUtils.getCssUrlFunctions(cssValue, '&quot;')
			
		then:
			results == expected
		
		where:
			cssValue									|| expected
			// absolute urls
			'url(http://some-url.com)'							|| [new CssUrlFunction(cssValue, 'url(', 		'http://some-url.com', 			')', 	'')]
			'url("http://some-url.com")'						|| [new CssUrlFunction(cssValue, 'url(', 		'http://some-url.com', 			')', 	'"')]
			"url('http://some-url.com')"						|| [new CssUrlFunction(cssValue, 'url(', 		'http://some-url.com', 			')', 	"'")]
			"url(&quot;http://some-url.com&quot;)"				|| [new CssUrlFunction(cssValue, 'url(', 		'http://some-url.com', 			')', 	"&quot;")]
			// relative urls
			'url(./relative-url)'								|| [new CssUrlFunction(cssValue, 'url(', 		'./relative-url', 				')', 	'')]
			'url("./relative-url")'								|| [new CssUrlFunction(cssValue, 'url(', 		'./relative-url', 				')', 	'"')]
			"url('./relative-url')"								|| [new CssUrlFunction(cssValue, 'url(', 		'./relative-url', 				')', 	"'")]
			"url(&quot;./relative-url&quot;)"					|| [new CssUrlFunction(cssValue, 'url(', 		'./relative-url', 				')', 	"&quot;")]
			
			'url(../relative-url)'								|| [new CssUrlFunction(cssValue, 'url(', 		'../relative-url', 				')', 	'')]
			'url("../relative-url")'							|| [new CssUrlFunction(cssValue, 'url(', 		'../relative-url', 				')', 	'"')]
			"url('../relative-url')"							|| [new CssUrlFunction(cssValue, 'url(', 		'../relative-url', 				')', 	"'")]
			"url(&quot;../relative-url&quot;)"					|| [new CssUrlFunction(cssValue, 'url(', 		'../relative-url', 				')', 	"&quot;")]
			// data uri
			'url(data:image/png;base64,ABC)'					|| [new CssUrlFunction(cssValue, 'url(', 		'data:image/png;base64,ABC', 	')', 	'')]
			'url("data:image/png;base64,ABC")'					|| [new CssUrlFunction(cssValue, 'url(', 		'data:image/png;base64,ABC', 	')', 	'"')]
			"url('data:image/png;base64,ABC')"					|| [new CssUrlFunction(cssValue, 'url(', 		'data:image/png;base64,ABC', 	')', 	"'")]
			"url(&quot;data:image/png;base64,ABC&quot;)"		|| [new CssUrlFunction(cssValue, 'url(',	 	'data:image/png;base64,ABC', 	')', 	"&quot;")]
			// preserve internal spaces
			'  url  (  http://some-url.com  )  '				|| [new CssUrlFunction(cssValue, '  url  (  ', 	'http://some-url.com', 			'  )  ', 	'')]
			'  url  (  "http://some-url.com"  )  '				|| [new CssUrlFunction(cssValue, '  url  (  ', 	'http://some-url.com', 			'  )  ', 	'"')]
			"  url  (  'http://some-url.com'  )  "				|| [new CssUrlFunction(cssValue, '  url  (  ', 	'http://some-url.com', 			'  )  ', 	"'")]
			"  url  (  &quot;http://some-url.com&quot;  )  "	|| [new CssUrlFunction(cssValue, '  url  (  ', 	'http://some-url.com', 			'  )  ', 	"&quot;")]
			// spaces in url
			"url(f o o.gif)"									|| []
			'url("f o o.gif")'									|| [new CssUrlFunction(cssValue, 'url(', 		"f o o.gif", 					')', 	'"')]
			"url('f o o.gif')"									|| [new CssUrlFunction(cssValue, 'url(', 		"f o o.gif", 					')', 	"'")]
			'url(&quot;f o o.gif&quot;)'						|| [new CssUrlFunction(cssValue, 'url(', 		"f o o.gif", 					')', 	'&quot;')]
			
			"url(f\\ o\\ o.gif)"								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\ o\\ o.gif", 				')', 	'')]
			'url("f\\ o\\ o.gif")'								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\ o\\ o.gif",				')', 	'"')]
			"url('f\\ o\\ o.gif')"								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\ o\\ o.gif",				')', 	"'")]
			'url(&quot;f\\ o\\ o.gif&quot;)'					|| [new CssUrlFunction(cssValue, 'url(', 		"f\\ o\\ o.gif",				')', 	'&quot;')]
			// parenthesis in url
			"url(f(o(o.gif)"									|| []
			'url("f(o(o.gif")'									|| [new CssUrlFunction(cssValue, 'url(', 		"f(o(o.gif", 					')', 	'"')]
			"url('f(o(o.gif')"									|| [new CssUrlFunction(cssValue, 'url(', 		"f(o(o.gif", 					')', 	"'")]
			'url(&quot;f(o(o.gif&quot;)'						|| [new CssUrlFunction(cssValue, 'url(', 		"f(o(o.gif", 					')', 	'&quot;')]
			
			"url(f(o)o.gif)"									|| []
			'url("f(o)o.gif")'									|| [new CssUrlFunction(cssValue, 'url(', 		"f(o)o.gif", 					')', 	'"')]
			"url('f(o)o.gif')"									|| [new CssUrlFunction(cssValue, 'url(', 		"f(o)o.gif", 					')', 	"'")]
			'url(&quot;f(o)o.gif&quot;)'						|| [new CssUrlFunction(cssValue, 'url(', 		"f(o)o.gif", 					')', 	'&quot;')]
			
			"url(f\\(o\\(o.gif)"								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\(o.gif", 				')', 	'')]
			'url("f\\(o\\(o.gif")'								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\(o.gif", 				')', 	'"')]
			"url('f\\(o\\(o.gif')"								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\(o.gif", 				')', 	"'")]
			'url(&quot;f\\(o\\)o.gif&quot;)'					|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\)o.gif", 				')', 	'&quot;')]
			
			"url(f\\(o\\)o.gif)"								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\)o.gif", 				')', 	'')]
			'url("f\\(o\\)o.gif")'								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\)o.gif", 				')', 	'"')]
			"url('f\\(o\\)o.gif')"								|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\)o.gif", 				')', 	"'")]
			'url(&quot;f\\(o\\(o.gif&quot;)'					|| [new CssUrlFunction(cssValue, 'url(', 		"f\\(o\\(o.gif", 				')', 	'&quot;')]
			// quotes in url
			"url(fo'o.gif)"										|| [new CssUrlFunction(cssValue, 'url(', 		"fo'o.gif",		 				')', 	'')]
			'url("fo\'o.gif")'									|| [new CssUrlFunction(cssValue, 'url(', 		"fo'o.gif",		 				')', 	'"')]
			/**"url('fo'o.gif')"									|| []*/
			'url(&quot;fo\'o.gif&quot;)'						|| [new CssUrlFunction(cssValue, 'url(', 		"fo'o.gif",		 				')', 	'&quot;')]

			'url(fo"o.gif)'										|| [new CssUrlFunction(cssValue, 'url(', 		'fo"o.gif', 					')', 	'')]
			/**'url("fo"o.gif")'									|| []*/
			"url('fo\"o.gif')"									|| [new CssUrlFunction(cssValue, 'url(', 		'fo"o.gif',		 				')', 	"'")]
			'url(&quot;fo"o.gif&quot;)'							|| [new CssUrlFunction(cssValue, 'url(', 		'fo"o.gif',		 				')', 	'&quot;')]
			
			'url(fo&quot;o.gif)'								|| [new CssUrlFunction(cssValue, 'url(', 		'fo&quot;o.gif', 				')', 	'')]
			'url("fo&quot;o.gif")'								|| [new CssUrlFunction(cssValue, 'url(', 		'fo&quot;o.gif',	 			')', 	'"')]
			"url('fo&quot;o.gif')"								|| [new CssUrlFunction(cssValue, 'url(', 		'fo&quot;o.gif',	 			')', 	"'")]
			/**'url(&quot;fo&quot;o.gif&quot;)'					|| []*/

			"url(fo\\'o.gif)"									|| [new CssUrlFunction(cssValue, 'url(', 		"fo\\'o.gif",		 			')', 	'')]
			'url("fo\\\'o.gif")'								|| [new CssUrlFunction(cssValue, 'url(', 		"fo\\'o.gif",		 			')', 	'"')]
			"url('fo\\'o.gif')"									|| [new CssUrlFunction(cssValue, 'url(', 		"fo\\'o.gif",		 			')', 	"'")]
			'url(&quot;fo\\\'o.gif&quot;)'						|| [new CssUrlFunction(cssValue, 'url(', 		"fo\\'o.gif",		 			')', 	'&quot;')]
			
			'url(fo\\"o.gif)'									|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\"o.gif', 					')', 	'')]
			'url("fo\\"o.gif")'									|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\"o.gif',		 			')', 	'"')]
			"url('fo\\\"o.gif')"								|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\"o.gif',		 			')', 	"'")]
			'url(&quot;fo\\"o.gif&quot;)'						|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\"o.gif',		 			')', 	'&quot;')]

			'url(fo\\&quot;o.gif)'								|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\&quot;o.gif', 				')', 	'')]
			'url("fo\\&quot;o.gif")'							|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\&quot;o.gif',	 			')', 	'"')]
			"url('fo\\&quot;o.gif')"							|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\&quot;o.gif',	 			')', 	"'")]
			'url(&quot;fo\\&quot;o.gif&quot;)'					|| [new CssUrlFunction(cssValue, 'url(', 		'fo\\&quot;o.gif',	 			')', 	'&quot;')]
			// may be invalid forms but depends on the context (CSS file or HTML style attribute)
			"url(images/h&quot;1.gif)"							|| [new CssUrlFunction(cssValue, 'url(', 		'images/h&quot;1.gif',			')', 	'')]
			"url(images/le'f.gif)"								|| [new CssUrlFunction(cssValue, 'url(', 		"images/le'f.gif",				')', 	'')]
			'url(images/le"f.gif)'								|| [new CssUrlFunction(cssValue, 'url(', 		'images/le"f.gif',				')', 	'')]
			// several urls
			"no-repeat center url(1), repeat-x url('2')"		|| [
																	new CssUrlFunction(' url(1),', ' url(', 	'1',	'),', 	''),
																	new CssUrlFunction(" url('2')", ' url(', 	'2',	')', 	"'")
																]
	}
	
	@Ignore("cases that are not handled and the results is uncertain")
	def "getCssUrlFunctions(#cssValue) invalid forms that are not handled"() {
		when:
			def results = HtmlUtils.getCssUrlFunctions(cssValue, '&quot;')
			
		then:
			results == expected
		
		where:
			cssValue									|| expected
			"url(images/h(1).gif)"								|| []
			'url("images/h"1.gif")'								|| []
			"url('images/h'1.gif')"								|| []
			"url(&quot;h&quot;1.gif&quot;)"						|| []
	}
}
