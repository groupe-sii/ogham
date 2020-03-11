package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Builder that configures resource resolution.
 * 
 * <p>
 * Resource resolution consists of finding a file:
 * <ul>
 * <li>either on filesystem</li>
 * <li>or in the classpath</li>
 * <li>or anywhere else</li>
 * </ul>
 * 
 * This implementation is used by {@link MessagingBuilder} for general
 * configuration. That configuration may be inherited (applied to other resource
 * resolution builders).
 * 
 * This implementation simply delegates to the
 * {@link ResourceResolutionBuilderHelper}.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class StandaloneResourceResolutionBuilder<P> extends AbstractParent<P> implements ResourceResolutionBuilder<StandaloneResourceResolutionBuilder<P>> {
	private ResourceResolutionBuilderHelper<StandaloneResourceResolutionBuilder<P>> helper;

	/**
	 * The builder is used alone (not in a context of a parent). In this case,
	 * parent is set to {@code null} meaning that {@link #and()} will return
	 * {@code null}.
	 * 
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public StandaloneResourceResolutionBuilder(BuildContext buildContext) {
		this(null, buildContext);
	}

	/**
	 * The builder is used by a parent builder. The parent is used when calling
	 * {@link #and()} method for chaining calls. The {@link EnvironmentBuilder}
	 * is used to evaluate properties at build time.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public StandaloneResourceResolutionBuilder(P parent, BuildContext buildContext) {
		super(parent);
		helper = new ResourceResolutionBuilderHelper<>(this, buildContext);
	}

	@Override
	public ClassPathResolutionBuilder<StandaloneResourceResolutionBuilder<P>> classpath() {
		return helper.classpath();
	}

	@Override
	public FileResolutionBuilder<StandaloneResourceResolutionBuilder<P>> file() {
		return helper.file();
	}

	@Override
	public StringResolutionBuilder<StandaloneResourceResolutionBuilder<P>> string() {
		return helper.string();
	}

	@Override
	public StandaloneResourceResolutionBuilder<P> resolver(ResourceResolver resolver) {
		return helper.resolver(resolver);
	}

}
