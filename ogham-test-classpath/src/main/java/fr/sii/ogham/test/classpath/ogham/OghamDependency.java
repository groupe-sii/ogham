package fr.sii.ogham.test.classpath.ogham;

import fr.sii.ogham.test.classpath.core.dependency.Dependency;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OghamDependency {
	CORE("ogham-core"),
	EMAIL_JAVAMAIL("ogham-email-javamail"),
	EMAIL_SENDGRID("ogham-email-sendgrid"),
	SMS_OVH("ogham-sms-ovh"),
	SMS_CLOUDHOPPER("ogham-sms-cloudhopper"),
	TEMPLATE_FREEMARKER("ogham-template-freemarker"),
	TEMPLATE_THYMELEAF("ogham-template-thymeleaf"),
	ALL("ogham-all"),
	SPRING_BOOT_AUTOCONFIGURE("ogham-spring-boot-autoconfigure"),
	SPRING_BOOT_STARTER_EMAIL("ogham-spring-boot-starter-email"),
	SPRING_BOOT_STARTER_SMS("ogham-spring-boot-starter-sms"),
	SPRING_BOOT_STARTER_ALL("ogham-spring-boot-starter-all");
	
	private final String artifactId;
	
	public Dependency toDependency(String version) {
		return new Dependency("fr.sii.ogham", artifactId, version);
	}
}
