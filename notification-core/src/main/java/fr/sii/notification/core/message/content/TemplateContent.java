package fr.sii.notification.core.message.content;

import fr.sii.notification.core.template.context.Context;

public class TemplateContent implements Content {
	private String path;
	
	private Context context;

	public TemplateContent(String path, Context context) {
		super();
		this.path = path;
		this.context = context;
	}

	public String getPath() {
		return path;
	}

	public Context getContext() {
		return context;
	}
}
