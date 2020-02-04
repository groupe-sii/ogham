package fr.sii.ogham.testing.assertion.util;

public interface Executable<E extends Exception> {
	void run() throws E;
}
