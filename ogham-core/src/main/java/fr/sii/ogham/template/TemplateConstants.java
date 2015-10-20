package fr.sii.ogham.template;

public class TemplateConstants {
	/**
	 * The prefix for properties used by the template engines
	 */
	public static final String PROPERTIES_PREFIX = "ogham.template";

	/**
	 * The property key for the prefix of the template resolution
	 */
	public static final String PREFIX_PROPERTY = PROPERTIES_PREFIX + ".prefix";

	/**
	 * The property key for the suffix of the template resolution
	 */
	public static final String SUFFIX_PROPERTY = PROPERTIES_PREFIX + ".suffix";

	
	private TemplateConstants() {
		super();
	}

}
