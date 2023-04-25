package oghamcore.it.core.builder.env.props

import spock.lang.TempDir

import java.nio.file.Path

import static fr.sii.ogham.core.util.IOUtils.copy
import static fr.sii.ogham.testing.util.ResourceUtils.resource

import org.junit.ClassRule
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import fr.sii.ogham.core.builder.env.props.PropsPath
import fr.sii.ogham.core.exception.builder.BuildException
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
class PropsPathSpec extends Specification {
	@TempDir @Shared File confFolder;
	
	def setupSpec() {
		confFolder.mkdirs();
		copy(resource("config/props-path.properties"), confFolder.toPath().resolve("props-path.properties").toFile())
	}

	def "PropsPath(#path).getProps() should #expectedDesc"() {
		given:
			def helper = new PropsPath(path, 0, 0)
			
		when:
			def props = helper.getProps()
		
		then:
			expected(props)
		
		where:
			path											|| expected							| expectedDesc
			"config/props-path.properties"					|| correctlyLoaded()				| "be loaded from classpath"
			"classpath:config/props-path.properties"		|| correctlyLoaded()				| "be loaded from classpath"
			"unexisting.properties?"						|| skipped()						| "be skipped as it doesn't exist but marked optional"
			"file:${confFolder}/props-path.properties"		|| correctlyLoaded()				| "be loaded from external location"
			"file:unexisting.properties?"					|| skipped()						| "be skipped as it doesn't exist but marked optional"
	}
	
	def "PropsPath(#path).getProps() should fail because file doesn't exist"() {
		given:
			def helper = new PropsPath(path, 0, 0)
			
		when:
			def props = helper.getProps()
		
		then:
			def e = thrown(BuildException)
			e.getCause() instanceof FileNotFoundException
		
		where:
			path << ["unexisting.properties", "classpath:unexisting.properties", "file:unexisting.properties"]
	}

	def correctlyLoaded() {
		{ props ->  props.getProperty("prop") == "value" }
	}
	
	def skipped() {
		{ props -> props.isEmpty() }
	}
}
