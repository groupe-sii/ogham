package fr.sii.ogham.core.builder.env.props;

import java.util.Properties;

public class Props extends AbstractProps {
	private final Properties properties;

	public Props(Properties properties, int priority, int index) {
		super(priority, index);
		this.properties = properties;
	}

	@Override
	public Properties getProps() {
		return properties;
	}
}