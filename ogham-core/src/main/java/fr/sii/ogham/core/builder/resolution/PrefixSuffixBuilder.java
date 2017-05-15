package fr.sii.ogham.core.builder.resolution;

public interface PrefixSuffixBuilder<MYSELF> {
	MYSELF pathPrefix(String... prefixes);
	
	MYSELF pathSuffix(String... suffixes);
}
