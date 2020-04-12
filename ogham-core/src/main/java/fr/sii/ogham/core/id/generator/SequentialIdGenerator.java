package fr.sii.ogham.core.id.generator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a simple sequence:
 * <ul>
 * <li>Either 1, 2, 3, ..., n</li>
 * <li>Or {@code <name>}1, {@code <name>}2, {@code <name>}3, ...,
 * {@code <name>}n</li>
 * </ul>
 * 
 * <p>
 * Name is URL encoded
 * 
 * <p>
 * If the generated id is greater than {@link Long#MAX_VALUE}, then it restarts
 * from 0.
 * 
 * @author Aurélien Baudet
 *
 */
public class SequentialIdGenerator implements IdGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(SequentialIdGenerator.class);

	private final AtomicLong idx;
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
		this.idx = new AtomicLong(initial);
		this.useNamePrefix = useNamePrefix;
	}

	@Override
	public String generate(String name) {
		long id = idx.getAndIncrement();
		if (id < 0) {
			id = id + Long.MIN_VALUE;
		}
		return namePrefix(name) + id;
	}

	private String namePrefix(String name) {
		if (!useNamePrefix) {
			return "";
		}
		try {
			return URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.warn("Failed to use {} as id prefix => no prefix used", name, e);
			return "";
		}
	}

}
