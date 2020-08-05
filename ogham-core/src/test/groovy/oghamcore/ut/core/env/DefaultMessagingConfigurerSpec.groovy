package oghamcore.ut.core.env

import java.util.function.Supplier

import fr.sii.ogham.core.builder.configurer.DefaultMessagingConfigurer
import fr.sii.ogham.core.builder.env.ConverterBuilder
import fr.sii.ogham.core.builder.env.EnvironmentBuilder
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class DefaultMessagingConfigurerSpec extends Specification {
	def "#profilesDesc #locationsDesc #expectedDesc"() {
		given:
			def files = []
			EnvironmentBuilder builder = Mock() {
				systemProperties() >> it
				converter() >> Mock(ConverterBuilder)
				properties(_) >> { args -> 
					files.add(args[0])
					it
				}
			}
			Supplier<List<String>> profilesSupplier = Mock() {
				get() >> profiles
			}
			Supplier<List<String>> locationsSupplier = Mock() {
				get() >> locations
			}
			def configurer = new DefaultMessagingConfigurer(profilesSupplier, locationsSupplier)
		
		when:
			configurer.configure(builder)
		
		then:
			files == expectedFiles()
		
		where:
			profiles 				| locations 					|| expectedDesc							| expectedFiles
			[]						| []							|| "should load default config files"	| {
					defaultFiles()
				}
			["foo"]					| []							|| "should load files for the profile first and config default config files"	| {
					defaultFilesForProfile("foo") +
					defaultFiles()
				}
			[]						| ["file:/app"]					|| "should load files from provided location frst and default config files"	| {
					filesInLocation("file:/app") +
					defaultFiles()
				}
			["foo"]					| ["file:/app"]					|| "should load files for the profile in provided location, files from provided location, files for the profile, default config files"	| {
					filesForProfileInLocation("foo", "file:/app") +
					filesInLocation("file:/app") +
					defaultFilesForProfile("foo") +
					defaultFiles()
				}	
			["foo", "bar"]			| []							|| "should load files for the first profile, files for the second profile and default config files"	| {
					defaultFilesForProfile("foo") +
					defaultFilesForProfile("bar") +
					defaultFiles()
				}
			[]						| ["file:/app", "file:/etc"]	|| "should load files from first location, files from second location and default config files"	| {
					filesInLocation("file:/app") +
					filesInLocation("file:/etc") +
					defaultFiles()
				}
			["foo", "bar"]			| ["file:/app", "file:/etc"]	|| "should load profiles in each custom locations, files in custom locations, default files for profiles and default files"	| {
					filesForProfileInLocation("foo", "file:/app") +
					filesForProfileInLocation("bar", "file:/app") +
					filesForProfileInLocation("foo", "file:/etc") +
					filesForProfileInLocation("bar", "file:/etc") +
					filesInLocation("file:/app") +
					filesInLocation("file:/etc") +
					defaultFilesForProfile("foo") +
					defaultFilesForProfile("bar") +
					defaultFiles()
				}	
				
			profilesDesc = profiles.isEmpty() ? "no active profiles" : "${profiles.join(' and ')} profiles enabled"
			locationsDesc = locations.isEmpty() ? "without user defined location" : "with ${locations.join(' and ')} configuration locations"
	
	}
	
	def defaultFiles() {
		[ 
			"?file:config", 
			"?file:config/ogham.properties",
			"?file:config/application.properties",
			"?classpath:config",
			"?classpath:config/ogham.properties",
			"?classpath:config/application.properties"
		]
	}
	
	def defaultFilesForProfile(def profile) {
		[
			"?file:config/ogham-${profile}.properties",
			"?file:config/application-${profile}.properties",
			"?classpath:config/ogham-${profile}.properties",
			"?classpath:config/application-${profile}.properties",
		]
	}
	
	def filesInLocation(def location) {
		[
			"?${location}",
			"?${location}/ogham.properties",
			"?${location}/application.properties",
		]
	}
	
	def filesForProfileInLocation(def profile, def location) {
		[
			"?${location}/ogham-${profile}.properties",
			"?${location}/application-${profile}.properties",
		]
	}
}
