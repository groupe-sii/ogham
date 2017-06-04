package fr.sii.ogham.spring.common;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class OghamResolutionProperties extends PrefixSuffixProperties {
	@NestedConfigurationProperty
	private PrefixSuffixProperties classpath = new PrefixSuffixProperties();
	@NestedConfigurationProperty
	private PrefixSuffixProperties file = new PrefixSuffixProperties();

	public PrefixSuffixProperties getClasspath() {
		return classpath;
	}

	public void setClasspath(PrefixSuffixProperties classpath) {
		this.classpath = classpath;
	}

	public PrefixSuffixProperties getFile() {
		return file;
	}

	public void setFile(PrefixSuffixProperties file) {
		this.file = file;
	}

}
