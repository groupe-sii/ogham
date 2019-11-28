package fr.sii.ogham.core.id.generator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates a simple sequence:
 * <ul>
 * <li>Either 1, 2, 3, ..., n</li>
 * <li>Or {@code <name>}1, {@code <name>}2, {@code <name>}3, ...,
 * {@code <name>}n</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class SequentialIdGenerator implements IdGenerator {
	private final AtomicInteger idx;
	private final boolean useNamePrefix;

	/**
	 * The sequence starts at 0 and doesn't use the name (only a number).
	 */
	public SequentialIdGenerator() {
		this(false);
	}

	/**
	 * The sequence starts at 0 and use the name only if {@code useNamePrefix}
	 * parameter is true.
	 * 
	 * @param useNamePrefix
	 *            use the name in the generated sequence
	 */
	public SequentialIdGenerator(boolean useNamePrefix) {
		this(useNamePrefix, 0);
	}

	/**
	 * The sequence starts at {@code initial} parameter value and use the name
	 * only if {@code useNamePrefix} parameter is true.
	 * 
	 * @param useNamePrefix
	 *            use the name in the generated sequence
	 * @param initial
	 *            the sequence start value
	 */
	public SequentialIdGenerator(boolean useNamePrefix, int initial) {
		super();
		this.idx = new AtomicInteger(initial);
		this.useNamePrefix = useNamePrefix;
	}

	@Override
	public String generate(String name) {
		return (useNamePrefix ? name : "") + idx.getAndIncrement();
	}

}
