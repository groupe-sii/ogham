package oghamcore.ut.core.resource.path;

import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver
import fr.sii.ogham.core.resource.path.UnresolvedPath
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class LookupAwareRelativePathResolverSpec extends Specification {
	
	def lookups = ['string': ['s:', 'string:'], 'classpath': ['classpath:', ''], 'file': ['file:']]
	
	def "path '#relativePath' relative to '#base' should be '#expected'"() {
		expect:
			new LookupAwareRelativePathResolver(lookups).resolve(new UnresolvedPath(base), relativePath).getOriginalPath().equals(expected)

		where:
			base                						| relativePath 		|| expected
			"" 											| "images/foo.gif"	|| "images/foo.gif"
			"template" 									| "images/foo.gif" 	|| "images/foo.gif"
			"template/"									| "images/foo.gif" 	|| "template/images/foo.gif"
			"template/thymeleaf"						| "images/foo.gif" 	|| "template/images/foo.gif"
			"template/thymeleaf/"						| "images/foo.gif" 	|| "template/thymeleaf/images/foo.gif"
			"/" 										| "images/foo.gif"	|| "/images/foo.gif"
			"/template" 								| "images/foo.gif" 	|| "/images/foo.gif"
			"/template/" 								| "images/foo.gif" 	|| "/template/images/foo.gif"
			"/template/thymeleaf"						| "images/foo.gif" 	|| "/template/images/foo.gif"
			"/template/thymeleaf/"						| "images/foo.gif" 	|| "/template/thymeleaf/images/foo.gif"
			"classpath:template" 						| "images/foo.gif" 	|| "classpath:images/foo.gif"
			"classpath:template/" 						| "images/foo.gif" 	|| "classpath:template/images/foo.gif"
			"classpath:template/thymeleaf" 				| "images/foo.gif" 	|| "classpath:template/images/foo.gif"
			"classpath:template/thymeleaf/"				| "images/foo.gif" 	|| "classpath:template/thymeleaf/images/foo.gif"
			"classpath:/template" 						| "images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/" 						| "images/foo.gif" 	|| "classpath:/template/images/foo.gif"
			"classpath:/template/thymeleaf" 			| "images/foo.gif" 	|| "classpath:/template/images/foo.gif"
			"classpath:/template/thymeleaf/" 			| "images/foo.gif" 	|| "classpath:/template/thymeleaf/images/foo.gif"
			"tpl.html"									| "images/foo.gif"	|| "images/foo.gif"
			"template/tpl.html"							| "images/foo.gif" 	|| "template/images/foo.gif"
			"template/thymeleaf/tpl.html"				| "images/foo.gif" 	|| "template/thymeleaf/images/foo.gif"
			"/tpl.html"									| "images/foo.gif"	|| "/images/foo.gif"
			"/template/tpl.html"						| "images/foo.gif" 	|| "/template/images/foo.gif"
			"/template/thymeleaf/tpl.html"				| "images/foo.gif" 	|| "/template/thymeleaf/images/foo.gif"
			"classpath:template/tpl.html"				| "images/foo.gif" 	|| "classpath:template/images/foo.gif"
			"classpath:template/thymeleaf/tpl.html"		| "images/foo.gif" 	|| "classpath:template/thymeleaf/images/foo.gif"
			"classpath:/template/tpl.html" 				| "images/foo.gif" 	|| "classpath:/template/images/foo.gif"
			"classpath:/template/thymeleaf/tpl.html"	| "images/foo.gif" 	|| "classpath:/template/thymeleaf/images/foo.gif"
	}

	def "absolute path '#relativePath' relative to '#base' should be '#expected'"() {
		expect:
			new LookupAwareRelativePathResolver(lookups).resolve(new UnresolvedPath(base), relativePath).getOriginalPath().equals(expected)

		where:
			base                						| relativePath 			|| expected
			"" 											| "/images/foo.gif"		|| "/images/foo.gif"
			"template" 									| "/images/foo.gif"		|| "/images/foo.gif"
			"template/"									| "/images/foo.gif" 	|| "/images/foo.gif"
			"template/thymeleaf"						| "/images/foo.gif" 	|| "/images/foo.gif"
			"template/thymeleaf/"						| "/images/foo.gif" 	|| "/images/foo.gif"
			"/" 										| "/images/foo.gif"		|| "/images/foo.gif"
			"/template" 								| "/images/foo.gif" 	|| "/images/foo.gif"
			"/template/" 								| "/images/foo.gif" 	|| "/images/foo.gif"
			"/template/thymeleaf"						| "/images/foo.gif" 	|| "/images/foo.gif"
			"/template/thymeleaf/"						| "/images/foo.gif" 	|| "/images/foo.gif"
			"classpath:template" 						| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:template/" 						| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:template/thymeleaf" 				| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:template/thymeleaf/"				| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template" 						| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/" 						| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/thymeleaf" 			| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/thymeleaf/" 			| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"tpl.html"									| "/images/foo.gif"		|| "/images/foo.gif"
			"template/tpl.html"							| "/images/foo.gif" 	|| "/images/foo.gif"
			"template/thymeleaf/tpl.html"				| "/images/foo.gif" 	|| "/images/foo.gif"
			"/tpl.html"									| "/images/foo.gif"		|| "/images/foo.gif"
			"/template/tpl.html"						| "/images/foo.gif" 	|| "/images/foo.gif"
			"/template/thymeleaf/tpl.html"				| "/images/foo.gif" 	|| "/images/foo.gif"
			"classpath:template/tpl.html"				| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:template/thymeleaf/tpl.html"		| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/tpl.html" 				| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/thymeleaf/tpl.html"	| "/images/foo.gif" 	|| "classpath:/images/foo.gif"
	}
	
	def "path '#relativePath' containing lookup relative to '#base' should be '#expected'"() {
		expect:
			new LookupAwareRelativePathResolver(lookups).resolve(new UnresolvedPath(base), relativePath).getOriginalPath().equals(expected)

		where:
			base                						| relativePath 					|| expected
			"" 											| "file:images/foo.gif"			|| "file:images/foo.gif"
			"template" 									| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"template/"									| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"/" 										| "file:images/foo.gif"			|| "file:images/foo.gif"
			"/template" 								| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"/template/" 								| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:template" 						| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:template/" 						| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:/template" 						| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:/template/" 						| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:template/tpl.html"				| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:template/thymeleaf/tpl.html"		| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:/template/tpl.html" 				| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:/template/thymeleaf/tpl.html"	| "file:images/foo.gif" 		|| "file:images/foo.gif"
			"classpath:template" 						| "classpath:images/foo.gif" 	|| "classpath:images/foo.gif"
			"classpath:template/" 						| "classpath:images/foo.gif" 	|| "classpath:template/images/foo.gif"
			"classpath:/template" 						| "classpath:images/foo.gif" 	|| "classpath:/images/foo.gif"
			"classpath:/template/" 						| "classpath:images/foo.gif" 	|| "classpath:/template/images/foo.gif"
			"classpath:template/tpl.html"				| "classpath:images/foo.gif" 	|| "classpath:template/images/foo.gif"
			"classpath:template/thymeleaf/tpl.html"		| "classpath:images/foo.gif" 	|| "classpath:template/thymeleaf/images/foo.gif"
			"classpath:/template/tpl.html" 				| "classpath:images/foo.gif" 	|| "classpath:/template/images/foo.gif"
			"classpath:/template/thymeleaf/tpl.html"	| "classpath:images/foo.gif" 	|| "classpath:/template/thymeleaf/images/foo.gif"
	}
}
