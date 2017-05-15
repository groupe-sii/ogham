package fr.sii.ogham.core.builder.resolution;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.RelativisableResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.util.BuilderUtils;

public abstract class AbstractSingleResolutionBuilder<MYSELF extends AbstractSingleResolutionBuilder<MYSELF, P>, P> extends AbstractParent<P> implements Builder<ResourceResolver> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSingleResolutionBuilder.class);
	
	protected List<String> lookups;
	protected List<String> pathPrefixes;
	protected List<String> pathSuffixes;
	protected EnvironmentBuilder<?> environmentBuilder;
	protected MYSELF myself;
	
	@SuppressWarnings("unchecked")
	protected AbstractSingleResolutionBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.environmentBuilder = environmentBuilder;
		lookups = new ArrayList<>();
		pathPrefixes = new ArrayList<>();
		pathSuffixes = new ArrayList<>();
	}

	public MYSELF lookup(String... prefix) {
		this.lookups.addAll(asList(prefix));
		return myself;
	}
	
	@Override
	public ResourceResolver build() throws BuildException {
		ResourceResolver resolver = createResolver();	
		if(!(resolver instanceof RelativisableResourceResolver)) {
			return resolver;
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		String resolvedPathPrefix = getValue(propertyResolver, pathPrefixes);
		String resolvedPathSuffix = getValue(propertyResolver, pathSuffixes);
		if(!resolvedPathPrefix.isEmpty() || !resolvedPathSuffix.isEmpty()) {
			LOG.debug("Using parentPath {} and extension {} for resource resolution", resolvedPathPrefix, resolvedPathSuffix);
			resolver = new RelativeResolver((RelativisableResourceResolver) resolver, resolvedPathPrefix, resolvedPathSuffix);
		}
		return resolver;
	}
	
	protected abstract ResourceResolver createResolver();
	
	public List<String> getLookups() {
		return lookups;
	}

	protected String getValue(PropertyResolver propertyResolver, List<String> props) {
		if(props==null) {
			return "";
		}
		String value = BuilderUtils.evaluate(props, propertyResolver, String.class);
		return value==null ? "" : value;
	}
}
