package fr.sii.ogham.core.builder.priority;

/**
 * Simple interface that provides a priority based on the value of an object.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object used as source for priority
 * @see AnnotationPriorityProvider
 */
public interface PriorityProvider<T> {
	/**
	 * Gives the priority number.
	 * 
	 * @param source
	 *            the source object that may be used to evaluate the priority
	 * @return the priority for the source
	 */
	int provide(T source);
}
