package fr.sii.ogham.testing.assertion.util;

/**
 * The aim of this registry is to fail fast (do not try all assertions if the
 * first one is KO).
 * 
 * It doesn't register the functions but immediately execute them.
 * 
 * @author Aurélien Baudet
 *
 */
public class FailImmediatelyRegistry implements AssertionRegistry {
	public <E extends Exception> void register(Executable<E> executable) throws E {
		executable.run();
	}

	public void execute() {
		// nothing to do
	}
}
