package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.template.context.Context;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Represent a content that original content comes from a template.
 * 
 * This is a decorator. It also implements several interfaces to delegate to decorated contents.
 * 
 * @author Aurélien Baudet
 *
 */
public class ParsedContent implements MayHaveStringContent, UpdatableStringContent, HasResourcePath {
	/**
	 * The template content used to generate the {@link Content}
	 */
	private final TemplateContent source;
	/**
	 * The content that has been generated
	 */
	private final Content generated;

	/**
	 * Initializes the content with template source and the generated content.
	 * 
	 * @param source
	 *            the source that has been processed
	 * @param generated
	 *            the generated output
	 */
	public ParsedContent(TemplateContent source, Content generated) {
		super();
		this.source = source;
		this.generated = generated;
	}

	/**
	 * Initializes the content with template source and the generated content.
	 * 
	 * @param sourcePath
	 *            the source path that has been processed
	 * @param sourceContext
	 *            the evaluation context
	 * @param generated
	 *            the generated output as string
	 */
	public ParsedContent(ResourcePath sourcePath, Context sourceContext, String generated) {
		super();
		this.source = new TemplateContent(sourcePath, sourceContext);
		this.generated = new StringContent(generated);
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public boolean canProvideString() {
		return generated instanceof MayHaveStringContent && ((MayHaveStringContent) generated).canProvideString();
	}

	@Override
	public String asString() {
		if(generated instanceof MayHaveStringContent && ((MayHaveStringContent) generated).canProvideString()) {
			return ((MayHaveStringContent) generated).asString();
		}
		return null;
	}

	@Override
	public void setStringContent(String content) {
		if(generated instanceof UpdatableStringContent) {
			((UpdatableStringContent) generated).setStringContent(content);
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(source).append(generated).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("source", "generated").isEqual();
	}

	@Override
	public ResourcePath getPath() {
		return source.getPath();
	}

	public TemplateContent getSource() {
		return source;
	}

	public Content getGenerated() {
		return generated;
	}
	
}
