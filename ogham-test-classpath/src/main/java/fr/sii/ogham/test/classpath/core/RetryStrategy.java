package fr.sii.ogham.test.classpath.core;

public interface RetryStrategy {

	<E extends Exception> void shouldRetry(E e) throws E, E, InterruptedException;

	long nextRetry();

}
