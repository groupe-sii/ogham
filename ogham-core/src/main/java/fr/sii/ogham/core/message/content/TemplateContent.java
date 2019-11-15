package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.template.context.NullContext;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Content that points to a template. The template contains variables. The
 * template will be evaluated with the provided context (variable values).
 * 
 * @author Aurélien Baudet
 *
 */
public class TemplateContent implements Content, HasResourcePath {
	/**
	 * The path to the template
	 */
	private ResourcePath path;

	/**
	 * The context (variable values)
	 */
	private Context context;

	
	/**
	 * Initialize the content with the path to the template and the context.
	 * 
	 * @param path
	 *            the path to the template
	 * @param context
	 *            the context (variable values)
	 */
	public TemplateContent(ResourcePath path, Context context) {
		super();
		this.path = path;
		this.context = context != null ? context : new NullContext();
	}

	/**
	 * Initialize the content with the path to the template and the context.
	 * 
	 * @param path
	 *            the path to the template as string
	 * @param context
	 *            the context (variable values)
	 */
	public TemplateContent(String path, Context context) {
		this(new UnresolvedPath(path), context);
	}

	/**
	 * Shortcut for directly using any object as source for variable
	 * substitutions.
	 * 
	 * @param path
	 *            the path to the template
	 * @param bean
	 *            the object that contains the variable values
	 */
	public TemplateContent(ResourcePath path, Object bean) {
		this(path, bean != null ? new BeanContext(bean) : new NullContext());
	}

	/**
	 * Shortcut for directly using any object as source for variable
	 * substitutions.
	 * 
	 * @param path
	 *            the path to the template
	 * @param bean
	 *            the object that contains the variable values
	 */
	public TemplateContent(String path, Object bean) {
		this(path, bean != null ? new BeanContext(bean) : new NullContext());
	}

	@Override
	public ResourcePath getPath() {
		return path;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateContent [path=").append(path).append(", context=").append(context).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(path, context).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("path", "context").isEqual();
	}
}
