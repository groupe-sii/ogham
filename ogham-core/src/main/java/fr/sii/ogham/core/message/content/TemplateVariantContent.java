package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.message.capability.HasVariant;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Content that points to a template variant. The template contains variables.
 * The template will be evaluated with the provided context (variable values).
 * <p>
 * The variant will be evaluated by the template parser to resolve real template
 * path.
 * 
 * @author Aurélien Baudet
 *
 */
public class TemplateVariantContent extends TemplateContent implements HasVariant {
	/**
	 * The template variant
	 */
	private final Variant variant;

	public TemplateVariantContent(String path, Variant variant, Context context) {
		this(new UnresolvedPath(path), variant, context);
	}

	public TemplateVariantContent(String path, Variant variant, Object bean) {
		this(new UnresolvedPath(path), variant, bean);
	}

	public TemplateVariantContent(ResourcePath path, Variant variant, Context context) {
		super(path, context);
		this.variant = variant;
	}

	public TemplateVariantContent(ResourcePath path, Variant variant, Object bean) {
		super(path, bean);
		this.variant = variant;
	}

	@Override
	public Variant getVariant() {
		return variant;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateVariantContent [path=").append(getPath()).append(", variant=").append(variant).append(", context=").append(getContext()).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getPath(), getContext(), getVariant()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("path", "context", "variant").isEqual();
	}
}
