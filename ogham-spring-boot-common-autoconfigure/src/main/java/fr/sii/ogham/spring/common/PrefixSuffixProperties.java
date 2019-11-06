package fr.sii.ogham.spring.common;

public class PrefixSuffixProperties {
	// @formatter:off
	/**
	 * You can set the path prefix for resource resolution. The aim is to define
	 * only the name of the resource (or a subset) and the system will find it
	 * for you. It avoids to explicitly write the whole path and let you change
	 * the resource resolution easily.
	 * 
	 * For example, you have one template located into
	 * "/foo/template/createAccount.html" and you have one template located into
	 * "/foo/template/resetPassword.html".
	 * 
	 * So you can set the prefix path to "/foo/template/". You can now reference
	 * the templates using only the file name: "createAccount.html" and
	 * "resetPassword.html".
	 * 
	 * Path prefix can be set globally, by template engine, by sending system
	 * and by resolver.
	 * For FreeMarker template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):
	 * 
	 * - (1) `ogham.email.freemarker.classpath.path-prefix`, 
	 * - (2) `ogham.email.template.classpath.path-prefix`,
	 * - (3) `ogham.email.freemarker.path-prefix`,
	 * - (4) `ogham.email.template.path-prefix`,
	 * - (5) `ogham.template.path-prefix`,
	 * - (6) `spring.freemarker.prefix`
	 * 
	 * For Thymeleaf template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):
	 * 
	 * - (1) `ogham.email.thymeleaf.classpath.path-prefix`, 
	 * - (2) `ogham.email.template.classpath.path-prefix`,
	 * - (3) `ogham.email.thymeleaf.path-prefix`,
	 * - (4) `ogham.email.template.path-prefix`,
	 * - (5) `ogham.template.path-prefix`,
	 * - (6) `spring.thymeleaf.prefix`
	 */
	// @formatter:on
	private String pathPrefix;

	// @formatter:off
	/**
	 * You can set the path suffix for resource resolution. The aim is to define
	 * only the name of the resource (or a subset) and the system will find it
	 * for you. It avoids to explicitly write the whole path and let you change
	 * the resource resolution easily.
	 * 
	 * For example, you have one template located into "createAccount.html" and
	 * you have one template located into "resetPassword.html".
	 * 
	 * So you can set the suffix path to ".html". You can now reference the
	 * templates using the file name: "createAccount" and "resetPassword".
	 * 
	 * Path suffix can be set globally, by template engine, by sending system
	 * and by resolver.
	 * For FreeMarker template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):
	 * 
	 * (1) `ogham.email.freemarker.classpath.path-suffix`, 
	 * (2) `ogham.email.template.classpath.path-suffix`,
	 * (3) `ogham.email.freemarker.path-suffix`,
	 * (4) `ogham.email.template.path-suffix`,
	 * (5) `ogham.template.path-suffix`,
	 * (6) `spring.freemarker.suffix`
	 * 
	 * For Thymeleaf template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):
	 * 
	 * (1) `ogham.email.thymeleaf.classpath.path-prefix`, 
	 * (2) `ogham.email.template.classpath.path-prefix`,
	 * (3) `ogham.email.thymeleaf.path-prefix`,
	 * (4) `ogham.email.template.path-prefix`,
	 * (5) `ogham.template.path-prefix`,
	 * (6) `spring.thymeleaf.prefix`
	 */
	// @formatter:on
	private String pathSuffix;

	public String getPathPrefix() {
		return pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public String getPathSuffix() {
		return pathSuffix;
	}

	public void setPathSuffix(String pathSuffix) {
		this.pathSuffix = pathSuffix;
	}

}