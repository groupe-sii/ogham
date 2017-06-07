package fr.sii.ogham.ut.core.mimetype.replace

import static spock.lang.Specification.thrown

import javax.activation.MimeType

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException
import fr.sii.ogham.core.mimetype.MimeTypeProvider
import fr.sii.ogham.core.mimetype.OverrideMimetypeProvider
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class OverrideMimetypeProviderSpec extends Specification {
	def "override mimetype #detectedMimetype by #newMimetype souhld be #expected"() {
		given:
			MimeTypeProvider detector = Mock();
			MimetypeReplacer replacer = Mock();
			detector.getMimeType(_) >> new MimeType(detectedMimetype)
			replacer.replace(_) >> newMimetype
			
		when:
			def result = new OverrideMimetypeProvider(detector, replacer).getMimeType(input);
			
		then:
			result.toString() == expected
			
		where:
			input  | detectedMimetype  | newMimetype            || expected
			"file" | "application/bar" | "application/bar"      || "application/bar"
			"file" | "application/bar" | "application/replaced" || "application/replaced"
	}
	
	def "override mimetype #detectedMimetype by #newMimetype souhld fail"() {
		given:
			MimeTypeProvider detector = Mock();
			MimetypeReplacer replacer = Mock();
			detector.getMimeType(_) >> new MimeType(detectedMimetype)
			replacer.replace(_) >> newMimetype
			
		when:
			new OverrideMimetypeProvider(detector, replacer).getMimeType(input);
			
		then:
			thrown(MimeTypeDetectionException)
			
		where:
			input  | detectedMimetype    | newMimetype
			"file" | "application/valid" | "invalid!" 
	}

}
