package fr.sii.ogham.it.core.mimetype

import static java.nio.charset.StandardCharsets.ISO_8859_1

import java.nio.charset.StandardCharsets

import org.apache.tika.Tika

import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder
import fr.sii.ogham.core.util.IOUtils
import spock.lang.Specification


class MimetypeProviderSpec extends Specification {
	def "MimetypeProvider.detect[stream](#file) == #expected"() {
		given:
			def builder = new SimpleMimetypeDetectionBuilder<>(null, new SimpleEnvironmentBuilder<>(null));
			def detector = builder
				.tika()
					.instance(new Tika())
					.failIfOctetStream(true)
					.and()
				.defaultMimetype("application/octet-stream")
				.build();
			
		when:
			def stream = getClass().getResourceAsStream("/mimetype/"+file)
			def mimetype = detector.detect(stream).toString();
			
		then:
			mimetype == expected
			
		where:
			file                            || expected
			"ogham-grey-900x900.png"        || "image/png"
	}
	
	def "MimetypeProvider.detect[string](#file) == #expected"() {
		given:
			def builder = new SimpleMimetypeDetectionBuilder<>(null, new SimpleEnvironmentBuilder<>(null));
			def detector = builder
				.tika()
					.instance(new Tika())
					.failIfOctetStream(true)
					.charset()
						.defaultCharset(charset.name())
						.and()
					.and()
				.defaultMimetype("application/octet-stream")
				.build();
			
		when:
			def content = IOUtils.toString(getClass().getResourceAsStream("/mimetype/"+file), charset)
			def mimetype = detector.detect(content).toString();
			
		then:
			mimetype == expected
			
		where:
			file                            | charset        || expected
			"ogham-grey-900x900.png"        | ISO_8859_1     || "image/png"
	}
	
	def "MimetypeProvider.detect[string](#file, #charset) == #expected"() {
		given:
			def builder = new SimpleMimetypeDetectionBuilder<>(null, new SimpleEnvironmentBuilder<>(null));
			def detector = builder
				.tika()
					.instance(new Tika())
					.failIfOctetStream(true)
					.and()
				.defaultMimetype("application/octet-stream")
				.build();
			
		when:
			def content = IOUtils.toString(getClass().getResourceAsStream("/mimetype/"+file), charset)
			def mimetype = detector.detect(content, charset).toString();
			
		then:
			mimetype == expected
			
		where:
			file                            | charset        || expected
			"ogham-grey-900x900.png"        | ISO_8859_1     || "image/png"
	}

}