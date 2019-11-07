package fr.sii.ogham.core.id.generator;

import java.util.concurrent.atomic.AtomicInteger;

public class SequentialIdGenerator implements IdGenerator {
	private final AtomicInteger idx;
	private final boolean useNamePrefix;

	public SequentialIdGenerator() {
		this(false);
	}

	public SequentialIdGenerator(boolean useNamePrefix) {
		this(useNamePrefix, 0);
	}
	
	public SequentialIdGenerator(boolean useNamePrefix, int initial) {
		super();
		this.idx = new AtomicInteger(initial);
		this.useNamePrefix = useNamePrefix;
	}
	

	@Override
	public String generate(String name) {
		return (useNamePrefix ? name : "")+idx.getAndIncrement();
	}

}
