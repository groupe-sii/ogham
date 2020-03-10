package fr.sii.ogham.core.builder.priority;

import fr.sii.ogham.core.builder.BuildContext;

/**
 * {@link PriorityProvider} used to encapsulate the default behavior for
 * providing priority value for a possible implementation.
 * 
 * <p>
 * If uses the underlying {@link AnnotationPriorityProvider} to provide the
 * priority value by scanning the class for the presence of {@link Priority}
 * annotation.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the kind of implementation
 */
public class ImplementationPriorityProvider<T> implements PriorityProvider<T> {
	private final AnnotationPriorityProvider annotationProvider;

	public ImplementationPriorityProvider(BuildContext buildContext) {
		super();
		annotationProvider = new AnnotationPriorityProvider(buildContext, new AutoDecrementPriorityProvider<>());
	}

	@Override
	public int provide(T source) {
		return annotationProvider.provide(source.getClass());
	}
}
