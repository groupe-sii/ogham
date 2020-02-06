package oghamtesting.ut.mock

import java.util.function.Predicate

import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.mock.classloader.FilterableClassLoader
import mock.context.SimpleBean
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class FilterableClassLoaderSpec extends Specification {
	def "matching name should be loaded using delegate ClassLoader"() {
		given:
			Class loadedClass = SimpleBean
			URL loadedResource = GroovyMock()
			Enumeration loadedResources = Mock()
			InputStream loadedStream = Mock()
			ClassLoader delegate = Mock {
				1 * loadClass("className") >> loadedClass
				1 * getResource("resourceName") >> loadedResource
				1 * getResources("resourceName") >> loadedResources
				1 * getResourceAsStream("resourceName") >> loadedStream
			}
			def classLoader = new FilterableClassLoader(delegate, (Predicate) { true })
	
		when:
			def klass = classLoader.loadClass("className")
			def resource = classLoader.getResource("resourceName")
			def resources = classLoader.getResources("resourceName")
			def stream = classLoader.getResourceAsStream("resourceName")
			
		then:
			klass == loadedClass
			resource == loadedResource
			resources == loadedResources
			stream == loadedStream
	}
	
	def "non matching class name should be throw ClassNotFoundException"() {
		given:
			ClassLoader delegate = Mock {
				0 * loadClass("className")
			}
			def classLoader = new FilterableClassLoader(delegate, (Predicate) { false })
	
		when:
			def klass = classLoader.loadClass("className")
			
		then:
			def e = thrown(ClassNotFoundException)
			e.message == "Class className not accepted"
	}

	def "non matching resource name should return null or empty enumeration"() {
		given:
			ClassLoader delegate = Mock {
				0 * getResource("resourceName")
				0 * getResources("resourceName")
				0 * getResourceAsStream("resourceName")
			}
			def classLoader = new FilterableClassLoader(delegate, (Predicate) { false })
	
		when:
			def resource = classLoader.getResource("resourceName")
			def resources = classLoader.getResources("resourceName")
			def stream = classLoader.getResourceAsStream("resourceName")
			
		then:
			resource == null
			resources == Collections.emptyEnumeration()
			stream == null
	}
}
