package fr.sii.ogham.core.builder;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.util.BuilderUtils;

public abstract class AbstractAutofillDefaultValueBuilder<MYSELF, P> extends AbstractParent<P> {
	protected MYSELF myself;
	protected List<String> defaultValueProperties;

	@SuppressWarnings("unchecked")
	public AbstractAutofillDefaultValueBuilder(Class<?> selfType, P parent) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		defaultValueProperties = new ArrayList<>();
	}

	public MYSELF defaultValueProperty(String... properties) {
		for(String prop : properties) {
			String propertyKey = BuilderUtils.isExpression(prop) ? BuilderUtils.getPropertyKey(prop) : prop;
			this.defaultValueProperties.add(propertyKey);
		}
		return myself;
	}
	
	public List<String> getDefaultValueProperties() {
		return defaultValueProperties;
	}
}
