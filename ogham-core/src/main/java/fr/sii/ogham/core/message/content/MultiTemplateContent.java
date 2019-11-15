package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.context.NullContext;

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
 * You can also specify all the extensions/variants to append to the template
 * path. By default, the extensions/variants will be {@link EmailVariant#HTML}
 * and {@link EmailVariant#TEXT}
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiTemplateContent extends MultiContent {

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context. You may also specify the extensions to use.
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param context
	 *            the context to share
	 * @param variants
	 *            the variants to specify
	 */
	public MultiTemplateContent(String templatePath, Context context, Variant... variants) {
		this(new UnresolvedPath(templatePath), context, variants);
	}

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context. It uses the default variants ({@link EmailVariant#HTML} and
	 * {@link EmailVariant#TEXT}).
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param context
	 *            the context to share
	 */
	public MultiTemplateContent(String templatePath, Context context) {
		this(new UnresolvedPath(templatePath), context);
	}

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context as simple bean. You may also specify the extensions/variants to
	 * use.
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param bean
	 *            the context to share as a simple POJO object
	 * @param extensions
	 *            the extensions to specify
	 */
	public MultiTemplateContent(String templatePath, Object bean, Variant... extensions) {
		this(new UnresolvedPath(templatePath), bean, extensions);
	}

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context as simple bean. It uses the default variants (
	 * {@link EmailVariant#HTML} and {@link EmailVariant#TEXT}).
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param bean
	 *            the context to share as a simple POJO object
	 */
	public MultiTemplateContent(String templatePath, Object bean) {
		this(new UnresolvedPath(templatePath), bean);
	}
	

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context. You may also specify the extensions to use.
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param context
	 *            the context to share
	 * @param variants
	 *            the variants to specify
	 */
	public MultiTemplateContent(ResourcePath templatePath, Context context, Variant... variants) {
		super(createTemplates(templatePath, context, variants));
	}

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context. It uses the default variants ({@link EmailVariant#HTML} and
	 * {@link EmailVariant#TEXT}).
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param context
	 *            the context to share
	 */
	public MultiTemplateContent(ResourcePath templatePath, Context context) {
		this(templatePath, context, EmailVariant.TEXT, EmailVariant.HTML);
	}

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context as simple bean. You may also specify the extensions/variants to
	 * use.
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param bean
	 *            the context to share as a simple POJO object
	 * @param extensions
	 *            the extensions to specify
	 */
	public MultiTemplateContent(ResourcePath templatePath, Object bean, Variant... extensions) {
		this(templatePath, bean != null ? new BeanContext(bean) : new NullContext(), extensions);
	}

	/**
	 * Initialize with the template path (without extension/variant) and the
	 * context as simple bean. It uses the default variants (
	 * {@link EmailVariant#HTML} and {@link EmailVariant#TEXT}).
	 * 
	 * @param templatePath
	 *            the path to the template (without extension/variant)
	 * @param bean
	 *            the context to share as a simple POJO object
	 */
	public MultiTemplateContent(ResourcePath templatePath, Object bean) {
		this(templatePath, bean != null ? new BeanContext(bean) : new NullContext());
	}

	private static TemplateContent[] createTemplates(ResourcePath templatePath, Context context, Variant[] variants) {
		TemplateContent[] contents = new TemplateContent[variants.length];
		for (int i = 0; i < variants.length; i++) {
			contents[i] = new TemplateVariantContent(templatePath, variants[i], context);
		}
		return contents;
	}
}
