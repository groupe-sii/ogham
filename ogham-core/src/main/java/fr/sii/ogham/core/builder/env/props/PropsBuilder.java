package fr.sii.ogham.core.builder.env.props;

import java.util.Properties;

import fr.sii.ogham.core.builder.env.PropertiesBuilder;

public class PropsBuilder extends AbstractProps {
	private final PropertiesBuilder<?> propertiesBuilder;

	public PropsBuilder(PropertiesBuilder<?> propertiesBuilder, int priority, int index) {
		super(priority, index);
		this.propertiesBuilder = propertiesBuilder;
	}

	@Override
	public Properties getProps() {
		return propertiesBuilder.build();
	}
}