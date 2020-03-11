package fr.sii.ogham.core.builder.registry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import fr.sii.ogham.core.clean.Cleanable;
import fr.sii.ogham.core.exception.clean.CleanException;
import fr.sii.ogham.core.exception.clean.MultipleCleanException;

/**
 * Registry that tracks instances that implements {@link Cleanable}. Other
 * instances are skipped.
 * 
 * <p>
 * The registry is also a {@link Cleanable} to relay cleanup request to
 * registered instances when calling {@link #clean()} method.
 * 
 * If an instance failed during its cleanup (exception is thrown), the failure
 * is registered. The next {@link Cleanable} instance is tried and so on until
 * all registered instances are cleaned. At the end a
 * {@link MultipleCleanException} is thrown with all registered failures.
 * 
 * @author Aurélien Baudet
 *
 */
public class CleanableRegistry implements Registry<Object>, Cleanable {
	private final Deque<Cleanable> cleanables;

	/**
	 * Initializes an empty registry
	 */
	public CleanableRegistry() {
		super();
		this.cleanables = new ArrayDeque<>();
	}

	@Override
	public void register(Object obj) {
		if (obj instanceof Cleanable) {
			cleanables.add((Cleanable) obj);
		}
	}

	@Override
	public void clean() throws CleanException {
		List<CleanException> failures = new ArrayList<>();
		while (!cleanables.isEmpty()) {
			clean(cleanables.pop(), failures);
		}
		if (!failures.isEmpty()) {
			throw new MultipleCleanException("Failed to cleanup several resources", failures);
		}
	}

	private static void clean(Cleanable cleanable, List<CleanException> failures) {
		try {
			cleanable.clean();
		} catch (CleanException e) {
			failures.add(e);
		}
	}
}
