package fr.sii.notification.core.message.content;

import fr.sii.notification.core.template.context.BeanContext;
import fr.sii.notification.core.template.context.Context;

public class MultiTemplateContent extends MultiContent {
	
	public MultiTemplateContent(String templatePath, Context context, String... extensions) {
		super(createTemplates(templatePath, context, extensions));
	}
	
	public MultiTemplateContent(String templatePath, Context context) {
		this(templatePath, context, "html", "txt");
	}
	
	public MultiTemplateContent(String templatePath, Object bean, String... extensions) {
		this(templatePath, new BeanContext(bean), extensions);
	}
	
	public MultiTemplateContent(String templatePath, Object bean) {
		this(templatePath, new BeanContext(bean));
	}
	
	private static TemplateContent[] createTemplates(String templatePath, Context context, String[] extensions) {
		TemplateContent[] contents = new TemplateContent[extensions.length];
		for(int i=0 ; i<extensions.length ; i++) {
			contents[i] = new TemplateContent(templatePath+"."+extensions[i], context);
		}
		return contents;
	}
}
