package fr.sii.notification.core.template;

import java.io.InputStream;

public class SimpleTemplate implements Template {

	private InputStream stream;
	
	public SimpleTemplate(InputStream stream) {
		super();
		this.stream = stream;
	}

	@Override
	public InputStream load() {
		return stream;
	}

}
