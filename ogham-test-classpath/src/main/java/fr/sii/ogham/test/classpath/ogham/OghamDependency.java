package fr.sii.ogham.test.classpath.ogham;

import static java.util.Arrays.asList;

import java.util.List;

import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import fr.sii.ogham.test.classpath.core.facet.Facet;
import lombok.Getter;

@Getter
public enum OghamDependency {
	CORE("ogham-core", Facet.CORE),
	EMAIL_JAVAMAIL("ogham-email-javamail", Facet.CORE, Facet.EMAIL_JAVAMAIL),
	EMAIL_SENDGRID("ogham-email-sendgrid", Facet.CORE, Facet.EMAIL_SENDGRID),
	SMS_OVH("ogham-sms-ovh", Facet.CORE, Facet.SMS_OVH),
	SMS_CLOUDHOPPER("ogham-sms-cloudhopper", Facet.CORE, Facet.SMS_CLOUDHOPPER),
	TEMPLATE_FREEMARKER("ogham-template-freemarker", Facet.CORE, Facet.TEMPLATE_FREEMARKER),
	TEMPLATE_THYMELEAF("ogham-template-thymeleaf", Facet.CORE, Facet.TEMPLATE_THYMELEAF),
	ALL("ogham-all", 
			Facet.CORE, 
			Facet.EMAIL_JAVAMAIL, 
			Facet.EMAIL_SENDGRID, 
			Facet.SMS_OVH,
			Facet.SMS_CLOUDHOPPER,
			Facet.TEMPLATE_FREEMARKER, 
			Facet.TEMPLATE_THYMELEAF),
	SPRING_BOOT_AUTOCONFIGURE("ogham-spring-boot-autoconfigure"),
	SPRING_BOOT_STARTER_EMAIL("ogham-spring-boot-starter-email", 
			Facet.CORE, 
			Facet.EMAIL_JAVAMAIL, 
			Facet.EMAIL_SENDGRID, 
			Facet.TEMPLATE_FREEMARKER, 
			Facet.TEMPLATE_THYMELEAF),
	SPRING_BOOT_STARTER_SMS("ogham-spring-boot-starter-sms", 
			Facet.CORE,
			Facet.SMS_OVH,
			Facet.SMS_CLOUDHOPPER,
			Facet.TEMPLATE_FREEMARKER, 
			Facet.TEMPLATE_THYMELEAF),
	SPRING_BOOT_STARTER_ALL("ogham-spring-boot-starter-all", 
			Facet.CORE, 
			Facet.EMAIL_JAVAMAIL, 
			Facet.EMAIL_SENDGRID, 
			Facet.SMS_OVH,
			Facet.SMS_CLOUDHOPPER,
			Facet.TEMPLATE_FREEMARKER, 
			Facet.TEMPLATE_THYMELEAF);
	
	private final String artifactId;
	private final Facet[] facets;
	
	
	public Dependency toDependency(String version) {
		return new Dependency("fr.sii.ogham", artifactId, version);
	}

	public List<Facet> getFacets() {
		return asList(facets);
	}
	
	public static OghamDependency fromArtifactId(String artifactId) {
		for(OghamDependency o : OghamDependency.values()) {
			if(o.getArtifactId().equals(artifactId)) {
				return o;
			}
		}
		throw new IllegalArgumentException("No matching OghamDependency for "+artifactId);
	}
	
	public static OghamDependency fromArtifactName(String artifactName) {
		for(OghamDependency o : OghamDependency.values()) {
			if(o.getArtifactId().equals("ogham-"+artifactName)) {
				return o;
			}
		}
		throw new IllegalArgumentException("No matching OghamDependency for "+artifactName);
	}
	
	

	private OghamDependency(String artifactId, Facet... facets) {
		this.artifactId = artifactId;
		this.facets = facets;
	}
}
