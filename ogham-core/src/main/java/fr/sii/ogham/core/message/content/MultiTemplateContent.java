package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.context.Context;

/**
 * <p>
 * A shortcut for using several templates with same context.
 * </p>
 * <p>
 * You have to put all templates to include at once at the same place. Each file
 * must be named identically. Only the extension must be different.
 * </p>
 * <p>
 * Then you only need to indicate the path to the templates without any
 * extension and provide the context.
 * </p>
 * <p>
 * You can also specify all the extensions to append to the template path. By
 * default, the extensions will be "html" and "txt"
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiTemplateContent extends MultiContent {

	/**
	 * Initialize with the template path (without extension) and the context.
	 * You may also specify the extensions to use.
	 * 
	 * @param templatePath
	 *            the path to the template (without extension)
	 * @param context
	 *            the context to share
	 * @param extensions
	 *            the extensions to specify
	 */
	public MultiTemplateContent(String templatePath, Context context, String... extensions) {
		super(createTemplates(templatePath, context, extensions));
	}

	/**
	 * Initialize with the template path (without extension) and the context. It
	 * uses the default extensions ("html" and "txt").
	 * 
	 * @param templatePath
	 *            the path to the template (without extension)
	 * @param context
	 *            the context to share
	 */
	public MultiTemplateContent(String templatePath, Context context) {
		this(templatePath, context, "html", "txt");
	}

	/**
	 * Initialize with the template path (without extension) and the context as
	 * simple bean. You may also specify the extensions to use.
	 * 
	 * @param templatePath
	 *            the path to the template (without extension)
	 * @param bean
	 *            the context to share as a simple POJO object
	 * @param extensions
	 *            the extensions to specify
	 */
	public MultiTemplateContent(String templatePath, Object bean, String... extensions) {
		this(templatePath, new BeanContext(bean), extensions);
	}

	/**
	 * Initialize with the template path (without extension) and the context as
	 * simple bean. It uses the default extensions ("html" and "txt").
	 * 
	 * @param templatePath
	 *            the path to the template (without extension)
	 * @param bean
	 *            the context to share as a simple POJO object
	 */
	public MultiTemplateContent(String templatePath, Object bean) {
		this(templatePath, new BeanContext(bean));
	}

	private static TemplateContent[] createTemplates(String templatePath, Context context, String[] extensions) {
		TemplateContent[] contents = new TemplateContent[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			contents[i] = new TemplateContent(templatePath + "." + extensions[i], context);
		}
		return contents;
	}
}
