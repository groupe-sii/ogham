package fr.sii.ogham.spring.common;

public class PrefixSuffixProperties {
	// @formatter:off
	/**
	 * You can set the path prefix for resource resolution. The aim is to define
	 * only the name of the resource (or a subset) and the system will find it
	 * for you. It avoids to explicitly write the whole path and let you change
	 * the resource resolution easily.<br /><br />
	 * 
	 * For example, you have one template located into
	 * "/foo/template/createAccount.html" and you have one template located into
	 * "/foo/template/resetPassword.html".<br /><br />
	 * 
	 * So you can set the prefix path to "/foo/template/". You can now reference
	 * the templates using only the file name: "createAccount.html" and
	 * "resetPassword.html".<br /><br />
	 * 
	 * Path prefix can be set globally, by template engine, by sending system
	 * and by resolver.
	 * For FreeMarker template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):<br /><br />
	 * 
	 * - (1) `ogham.email.freemarker.classpath.path-prefix`,<br />
	 * - (2) `ogham.email.template.classpath.path-prefix`,<br />
	 * - (3) `ogham.email.freemarker.path-prefix`,<br />
	 * - (4) `ogham.email.template.path-prefix`,<br />
	 * - (5) `ogham.template.path-prefix`,<br />
	 * - (6) `spring.freemarker.prefix`<br /><br />
	 * 
	 * For Thymeleaf template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):<br /><br />
	 * 
	 * - (1) `ogham.email.thymeleaf.classpath.path-prefix`, <br />
	 * - (2) `ogham.email.template.classpath.path-prefix`,<br />
	 * - (3) `ogham.email.thymeleaf.path-prefix`,<br />
	 * - (4) `ogham.email.template.path-prefix`,<br />
	 * - (5) `ogham.template.path-prefix`,<br />
	 * - (6) `spring.thymeleaf.prefix`<br />
	 */
	// @formatter:on
	private String pathPrefix;

	// @formatter:off
	/**
	 * You can set the path suffix for resource resolution. The aim is to define
	 * only the name of the resource (or a subset) and the system will find it
	 * for you. It avoids to explicitly write the whole path and let you change
	 * the resource resolution easily.<br /><br />
	 * 
	 * For example, you have one template located into "createAccount.html" and
	 * you have one template located into "resetPassword.html".<br /><br />
	 * 
	 * So you can set the suffix path to ".html". You can now reference the
	 * templates using the file name: "createAccount" and "resetPassword".<br /><br />
	 * 
	 * Path suffix can be set globally, by template engine, by sending system
	 * and by resolver.
	 * For FreeMarker template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):<br /><br />
	 * 
	 * (1) `ogham.email.freemarker.classpath.path-suffix`, <br />
	 * (2) `ogham.email.template.classpath.path-suffix`,<br />
	 * (3) `ogham.email.freemarker.path-suffix`,<br />
	 * (4) `ogham.email.template.path-suffix`,<br />
	 * (5) `ogham.template.path-suffix`,<br />
	 * (6) `spring.freemarker.suffix`<br />
	 * 
	 * For Thymeleaf template engine, the priority order for
	 * email and template loaded from classpath is (from highest priority to lowest):<br /><br />
	 * 
	 * (1) `ogham.email.thymeleaf.classpath.path-prefix`, <br />
	 * (2) `ogham.email.template.classpath.path-prefix`,<br />
	 * (3) `ogham.email.thymeleaf.path-prefix`,<br />
	 * (4) `ogham.email.template.path-prefix`,<br />
	 * (5) `ogham.template.path-prefix`,<br />
	 * (6) `spring.thymeleaf.prefix`<br />
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