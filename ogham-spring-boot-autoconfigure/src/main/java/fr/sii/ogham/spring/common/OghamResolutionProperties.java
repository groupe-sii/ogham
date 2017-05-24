package fr.sii.ogham.spring.common;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class OghamResolutionProperties {
	@NestedConfigurationProperty
	private PrefixSuffixProperties classpath;
	@NestedConfigurationProperty
	private PrefixSuffixProperties file;
	private String prefix;
	private String suffix;

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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
