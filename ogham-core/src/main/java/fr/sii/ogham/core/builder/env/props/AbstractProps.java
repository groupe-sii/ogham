package fr.sii.ogham.core.builder.env.props;

import java.util.Properties;

public abstract class AbstractProps {
	protected final int priority;
	protected final int index;

	protected AbstractProps(int priority, int index) {
		this.priority = priority;
		this.index = index;
	}

	public abstract Properties getProps();

	public int getPriority() {
		return priority;
	}

	public int getIndex() {
		return index;
	}
}